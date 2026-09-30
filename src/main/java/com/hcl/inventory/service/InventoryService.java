package com.hcl.inventory.service;

import com.hcl.inventory.model.Inventory;
import com.hcl.inventory.model.Product;
import com.hcl.inventory.model.Warehouse;
import com.hcl.inventory.repository.InventoryRepository;
import com.hcl.inventory.repository.ProductRepository;
import com.hcl.inventory.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getStockByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    public List<Inventory> getStockByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory record not found with id: " + id));
    }

    // Creates the initial stock record for a product in a warehouse.
    // Fails if one already exists for that product+warehouse pair — use
    // updateQuantity() or adjustQuantity() to change an existing record.
    public Inventory createInventory(Inventory inventory) {
        if (inventory.getProductId() == null || inventory.getWarehouseId() == null) {
            throw new IllegalArgumentException("productId and warehouseId are required");
        }
        if (inventoryRepository.existsByProductIdAndWarehouseId(inventory.getProductId(), inventory.getWarehouseId())) {
            throw new IllegalArgumentException("Inventory already exists for this product in this warehouse — use update instead");
        }

        Product product = resolveProduct(inventory.getProductId());
        Warehouse warehouse = resolveWarehouse(inventory.getWarehouseId());

        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        return inventoryRepository.save(inventory);
    }

    // Sets the quantity to an exact value (e.g. after a stock count/audit).
    public Inventory updateQuantity(Long id, Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            throw new IllegalArgumentException("quantity must be zero or positive");
        }
        Inventory existing = getInventoryById(id);
        existing.setQuantity(newQuantity);
        return inventoryRepository.save(existing);
    }

    // Adjusts the quantity by a delta (positive to add stock, negative to
    // remove it). Requires an existing inventory row — throws if the
    // product has never been stocked in that warehouse before.
    @Transactional
    public Inventory adjustQuantity(Long productId, Long warehouseId, int delta) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No inventory record for product " + productId + " in warehouse " + warehouseId));
        return applyDelta(inventory, delta);
    }

    // Same as adjustQuantity(), but creates a new inventory row (starting
    // from 0) if one doesn't exist yet, instead of throwing. Used by
    // StockMovement for IN / TRANSFER movements, where a warehouse may be
    // receiving a given product for the very first time.
    @Transactional
    public Inventory adjustOrCreateQuantity(Long productId, Long warehouseId, int delta) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> {
                    Inventory fresh = new Inventory();
                    fresh.setProduct(resolveProduct(productId));
                    fresh.setWarehouse(resolveWarehouse(warehouseId));
                    fresh.setQuantity(0);
                    return fresh;
                });
        return applyDelta(inventory, delta);
    }

    private Inventory applyDelta(Inventory inventory, int delta) {
        int newQuantity = inventory.getQuantity() + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock: cannot reduce below zero");
        }
        inventory.setQuantity(newQuantity);
        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Inventory record not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
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