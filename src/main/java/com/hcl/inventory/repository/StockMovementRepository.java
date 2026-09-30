package com.hcl.inventory.repository;

import com.hcl.inventory.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductId(Long productId);

    List<StockMovement> findByFromWarehouseId(Long warehouseId);

    List<StockMovement> findByToWarehouseId(Long warehouseId);
}