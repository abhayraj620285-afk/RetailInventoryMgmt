package com.hcl.inventory.service;

import com.hcl.inventory.model.Warehouse;
import com.hcl.inventory.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));
    }

    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouseRepository.existsByName(warehouse.getName())) {
            throw new IllegalArgumentException("A warehouse named '" + warehouse.getName() + "' already exists");
        }
        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse(Long id, Warehouse updatedWarehouse) {
        Warehouse existing = getWarehouseById(id);

        if (!existing.getName().equals(updatedWarehouse.getName())
                && warehouseRepository.existsByName(updatedWarehouse.getName())) {
            throw new IllegalArgumentException("A warehouse named '" + updatedWarehouse.getName() + "' already exists");
        }

        existing.setName(updatedWarehouse.getName());
        existing.setLocation(updatedWarehouse.getLocation());
        existing.setCapacity(updatedWarehouse.getCapacity());

        return warehouseRepository.save(existing);
    }

    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new EntityNotFoundException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }
}