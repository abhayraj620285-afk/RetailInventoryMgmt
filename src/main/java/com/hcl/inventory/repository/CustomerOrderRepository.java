package com.hcl.inventory.repository;

import com.hcl.inventory.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByWarehouseId(Long warehouseId);

    List<CustomerOrder> findByUserId(Long userId);

    List<CustomerOrder> findByStatus(CustomerOrder.Status status);
}