package com.hcl.inventory.controller;

import com.hcl.inventory.model.PurchaseOrder;
import com.hcl.inventory.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllOrders());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>> getOrdersBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersBySupplier(supplierId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getOrderById(id));
    }

    // Body example:
    // {
    //   "supplierId": 1,
    //   "warehouseId": 2,
    //   "items": [
    //     {"productId": 3, "quantity": 50, "unitCost": 120.00},
    //     {"productId": 4, "quantity": 20, "unitCost": 75.50}
    //   ]
    // }
    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@Valid @RequestBody PurchaseOrder order) {
        PurchaseOrder saved = purchaseOrderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}/mark-ordered")
    public ResponseEntity<PurchaseOrder> markAsOrdered(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.markAsOrdered(id));
    }

    // Adds the stock to the order's warehouse and marks it RECEIVED.
    @PatchMapping("/{id}/mark-received")
    public ResponseEntity<PurchaseOrder> markAsReceived(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.markAsReceived(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrder> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.cancelOrder(id));
    }
}