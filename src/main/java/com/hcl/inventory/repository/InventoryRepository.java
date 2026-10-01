package com.hcl.inventory.repository;

import com.hcl.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByWarehouse_Id(Long warehouseId);

    List<Inventory> findByProduct_Id(Long productId);

    Optional<Inventory> findByProduct_IdAndWarehouse_Id(
            Long productId,
            Long warehouseId
    );

    boolean existsByProduct_IdAndWarehouse_Id(
            Long productId,
            Long warehouseId
    );
}