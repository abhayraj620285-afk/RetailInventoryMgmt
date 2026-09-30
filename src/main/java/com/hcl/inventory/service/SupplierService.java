package com.hcl.inventory.service;

import com.hcl.inventory.model.Supplier;
import com.hcl.inventory.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        Supplier existing = getSupplierById(id);
        existing.setName(updatedSupplier.getName());
        existing.setContactEmail(updatedSupplier.getContactEmail());
        existing.setPhone(updatedSupplier.getPhone());
        existing.setLeadTimeDays(updatedSupplier.getLeadTimeDays());
        return supplierRepository.save(existing);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new EntityNotFoundException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
}