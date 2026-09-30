package com.hcl.inventory.controller;

import com.hcl.inventory.model.Inventory;
import com.hcl.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    // View stock levels by warehouse — matches the README's API overview.
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<Inventory>> getStockByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getStockByWarehouse(warehouseId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Inventory>> getStockByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getStockByProduct(productId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@Valid @RequestBody Inventory inventory) {
        Inventory saved = inventoryService.createInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Sets quantity to an exact value, e.g. { "quantity": 120 }
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(inventoryService.updateQuantity(id, body.get("quantity")));
    }

    // Adjusts stock by a delta, e.g. { "delta": -5 } for a sale, { "delta": 20 } for a restock.
    @PatchMapping("/adjust")
    public ResponseEntity<Inventory> adjustQuantity(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(inventoryService.adjustQuantity(productId, warehouseId, body.get("delta")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}