package com.hcl.inventory.service;

import com.hcl.inventory.model.Product;
import com.hcl.inventory.model.StockMovement;
import com.hcl.inventory.model.StockMovement.MovementType;
import com.hcl.inventory.model.Warehouse;
import com.hcl.inventory.repository.ProductRepository;
import com.hcl.inventory.repository.StockMovementRepository;
import com.hcl.inventory.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;

    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }

    public List<StockMovement> getMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProduct_Id(productId);
    }

    public StockMovement getMovementById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock movement not found with id: " + id));
    }

    // Records a movement AND applies its effect on Inventory, atomically.
    // If either step fails, nothing is saved (thanks to @Transactional).
    @Transactional
    public StockMovement recordMovement(StockMovement movement) {
        if (movement.getProductId() == null) {
            throw new IllegalArgumentException("productId is required");
        }
        if (movement.getType() == null) {
            throw new IllegalArgumentException("type is required (IN, OUT, or TRANSFER)");
        }
        if (movement.getQuantity() == null || movement.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity must be a positive number");
        }

        Product product = resolveProduct(movement.getProductId());
        movement.setProduct(product);
        movement.setTimestamp(LocalDateTime.now());

        switch (movement.getType()) {
            case IN -> applyIn(movement);
            case OUT -> applyOut(movement);
            case TRANSFER -> applyTransfer(movement);
        }

        return stockMovementRepository.save(movement);
    }

    private void applyIn(StockMovement movement) {
        if (movement.getToWarehouseId() == null) {
            throw new IllegalArgumentException("toWarehouseId is required for an IN movement");
        }
        Warehouse to = resolveWarehouse(movement.getToWarehouseId());
        inventoryService.adjustOrCreateQuantity(movement.getProduct().getId(), to.getId(), movement.getQuantity());
        movement.setToWarehouse(to);
    }

    private void applyOut(StockMovement movement) {
        if (movement.getFromWarehouseId() == null) {
            throw new IllegalArgumentException("fromWarehouseId is required for an OUT movement");
        }
        Warehouse from = resolveWarehouse(movement.getFromWarehouseId());
        // No "or create" here — you can't remove stock from a warehouse
        // that never had any, so this throws if no inventory row exists.
        inventoryService.adjustQuantity(movement.getProduct().getId(), from.getId(), -movement.getQuantity());
        movement.setFromWarehouse(from);
    }

    private void applyTransfer(StockMovement movement) {
        if (movement.getFromWarehouseId() == null || movement.getToWarehouseId() == null) {
            throw new IllegalArgumentException("fromWarehouseId and toWarehouseId are both required for a TRANSFER");
        }
        if (movement.getFromWarehouseId().equals(movement.getToWarehouseId())) {
            throw new IllegalArgumentException("fromWarehouseId and toWarehouseId must be different");
        }

        Warehouse from = resolveWarehouse(movement.getFromWarehouseId());
        Warehouse to = resolveWarehouse(movement.getToWarehouseId());
        Long productId = movement.getProduct().getId();

        // Decrement source first — if source doesn't have enough stock,
        // this throws and the destination is never touched (rolled back).
        inventoryService.adjustQuantity(productId, from.getId(), -movement.getQuantity());
        inventoryService.adjustOrCreateQuantity(productId, to.getId(), movement.getQuantity());

        movement.setFromWarehouse(from);
        movement.setToWarehouse(to);
    }

    private Product resolveProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }

    private Warehouse resolveWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + warehouseId));
    }
}