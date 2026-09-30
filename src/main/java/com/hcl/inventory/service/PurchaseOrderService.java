package com.hcl.inventory.service;

import com.hcl.inventory.model.*;
import com.hcl.inventory.model.PurchaseOrder.Status;
import com.hcl.inventory.repository.ProductRepository;
import com.hcl.inventory.repository.PurchaseOrderRepository;
import com.hcl.inventory.repository.SupplierRepository;
import com.hcl.inventory.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrder> getOrdersBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId);
    }

    public PurchaseOrder getOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + id));
    }

    // Creates a PurchaseOrder with its line items in one go. Expects each
    // item's productId to be set; resolves everything before saving.
    @Transactional
    public PurchaseOrder createOrder(PurchaseOrder order) {
        if (order.getSupplierId() == null) {
            throw new IllegalArgumentException("supplierId is required");
        }
        if (order.getWarehouseId() == null) {
            throw new IllegalArgumentException("warehouseId is required (which warehouse will receive the stock)");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("A purchase order must have at least one item");
        }

        Supplier supplier = supplierRepository.findById(order.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + order.getSupplierId()));
        Warehouse warehouse = warehouseRepository.findById(order.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + order.getWarehouseId()));

        order.setSupplier(supplier);
        order.setWarehouse(warehouse);
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());

        for (PurchaseOrderItem item : order.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Each item must have a productId");
            }
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + item.getProductId()));
            item.setProduct(product);
            item.setPurchaseOrder(order);
        }

        return purchaseOrderRepository.save(order);
    }

    // Moves the order to ORDERED (just a status change, no stock effect).
    public PurchaseOrder markAsOrdered(Long id) {
        PurchaseOrder order = getOrderById(id);
        if (order.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Only a PENDING order can be marked as ORDERED");
        }
        order.setStatus(Status.ORDERED);
        return purchaseOrderRepository.save(order);
    }

    // The important one: marks the order RECEIVED and actually adds the
    // stock to the target warehouse, one adjustment per line item.
    @Transactional
    public PurchaseOrder markAsReceived(Long id) {
        PurchaseOrder order = getOrderById(id);
        if (order.getStatus() == Status.RECEIVED) {
            throw new IllegalArgumentException("This order has already been received");
        }
        if (order.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("A cancelled order cannot be received");
        }

        for (PurchaseOrderItem item : order.getItems()) {
            inventoryService.adjustOrCreateQuantity(
                    item.getProduct().getId(),
                    order.getWarehouse().getId(),
                    item.getQuantity()
            );
        }

        order.setStatus(Status.RECEIVED);
        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder cancelOrder(Long id) {
        PurchaseOrder order = getOrderById(id);
        if (order.getStatus() == Status.RECEIVED) {
            throw new IllegalArgumentException("A received order cannot be cancelled");
        }
        order.setStatus(Status.CANCELLED);
        return purchaseOrderRepository.save(order);
    }
}