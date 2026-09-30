package com.hcl.inventory.repository;

import com.hcl.inventory.model.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByProductId(Long productId);
}