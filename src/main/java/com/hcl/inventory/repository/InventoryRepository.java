package com.hcl.inventory.repository;

import com.hcl.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByWarehouseId(Long warehouseId);

    List<Inventory> findByProductId(Long productId);

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);
}