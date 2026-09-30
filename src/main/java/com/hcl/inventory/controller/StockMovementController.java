package com.hcl.inventory.controller;

import com.hcl.inventory.model.StockMovement;
import com.hcl.inventory.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<List<StockMovement>> getAllMovements() {
        return ResponseEntity.ok(stockMovementService.getAllMovements());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovement>> getMovementsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByProduct(productId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovement> getMovementById(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementService.getMovementById(id));
    }

    // Body examples:
    //   IN:       {"productId":1,"toWarehouseId":2,"quantity":50,"type":"IN"}
    //   OUT:      {"productId":1,"fromWarehouseId":2,"quantity":10,"type":"OUT"}
    //   TRANSFER: {"productId":1,"fromWarehouseId":2,"toWarehouseId":3,"quantity":20,"type":"TRANSFER"}
    @PostMapping
    public ResponseEntity<StockMovement> recordMovement(@Valid @RequestBody StockMovement movement) {
        StockMovement saved = stockMovementService.recordMovement(movement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}