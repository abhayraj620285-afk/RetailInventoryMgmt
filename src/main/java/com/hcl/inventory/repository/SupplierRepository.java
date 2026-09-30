package com.hcl.inventory.repository;

import com.hcl.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}