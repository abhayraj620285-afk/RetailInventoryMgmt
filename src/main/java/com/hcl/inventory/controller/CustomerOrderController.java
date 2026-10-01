package com.hcl.inventory.controller;

import com.hcl.inventory.model.CustomerOrder;
import com.hcl.inventory.service.CustomerOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @GetMapping
    public ResponseEntity<List<CustomerOrder>> getAllOrders() {
        return ResponseEntity.ok(customerOrderService.getAllOrders());
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<CustomerOrder>> getOrdersByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(customerOrderService.getOrdersByWarehouse(warehouseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(customerOrderService.getOrderById(id));
    }


    @PostMapping
    public ResponseEntity<CustomerOrder> placeOrder(@Valid @RequestBody CustomerOrder order) {
        CustomerOrder saved = customerOrderService.placeOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}/mark-shipped")
    public ResponseEntity<CustomerOrder> markAsShipped(@PathVariable Long id) {
        return ResponseEntity.ok(customerOrderService.markAsShipped(id));
    }

    @PatchMapping("/{id}/mark-delivered")
    public ResponseEntity<CustomerOrder> markAsDelivered(@PathVariable Long id) {
        return ResponseEntity.ok(customerOrderService.markAsDelivered(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<CustomerOrder> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(customerOrderService.cancelOrder(id));
    }
}