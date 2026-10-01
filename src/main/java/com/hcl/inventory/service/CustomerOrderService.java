package com.hcl.inventory.service;

import com.hcl.inventory.model.*;
import com.hcl.inventory.model.CustomerOrder.Status;
import com.hcl.inventory.repository.CustomerOrderRepository;
import com.hcl.inventory.repository.ProductRepository;
import com.hcl.inventory.repository.UserRepository;
import com.hcl.inventory.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public List<CustomerOrder> getAllOrders() {
        return customerOrderRepository.findAll();
    }

    public List<CustomerOrder> getOrdersByWarehouse(Long warehouseId) {
        return customerOrderRepository.findByWarehouseId(warehouseId);
    }

    public CustomerOrder getOrderById(Long id) {
        return customerOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer order not found with id: " + id));
    }

    // Places an order: validates stock for every item FIRST (no partial
    // fulfillment), only then decrements stock and saves. If any single
    // item is short, the whole order is rejected and nothing is touched.
    @Transactional
    public CustomerOrder placeOrder(CustomerOrder order) {
        if (order.getWarehouseId() == null) {
            throw new IllegalArgumentException("warehouseId is required (which warehouse fulfills this order)");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("An order must have at least one item");
        }

        Warehouse warehouse = warehouseRepository.findById(order.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + order.getWarehouseId()));

        User user = null;
        if (order.getUserId() != null) {
            user = userRepository.findById(order.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + order.getUserId()));
        }

        // Resolve products and validate stock BEFORE making any changes.
        for (OrderItem item : order.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Each item must have a productId");
            }
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + item.getProductId()));

            int available = inventoryService.getAvailableQuantity(product.getId(), warehouse.getId());
            if (available < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product '" + product.getName() + "': requested "
                                + item.getQuantity() + ", available " + available);
            }
            item.setProduct(product);
            item.setCustomerOrder(order);
        }

        // All items validated — now it's safe to actually decrement stock.
        for (OrderItem item : order.getItems()) {
            inventoryService.adjustQuantity(item.getProduct().getId(), warehouse.getId(), -item.getQuantity());
        }

        order.setWarehouse(warehouse);
        order.setUser(user);
        order.setStatus(Status.PLACED);
        order.setOrderDate(LocalDateTime.now());

        return customerOrderRepository.save(order);
    }

    public CustomerOrder markAsShipped(Long id) {
        CustomerOrder order = getOrderById(id);
        if (order.getStatus() != Status.PLACED) {
            throw new IllegalArgumentException("Only a PLACED order can be marked as SHIPPED");
        }
        order.setStatus(Status.SHIPPED);
        return customerOrderRepository.save(order);
    }

    public CustomerOrder markAsDelivered(Long id) {
        CustomerOrder order = getOrderById(id);
        if (order.getStatus() != Status.SHIPPED) {
            throw new IllegalArgumentException("Only a SHIPPED order can be marked as DELIVERED");
        }
        order.setStatus(Status.DELIVERED);
        return customerOrderRepository.save(order);
    }

    // Cancelling a PLACED or SHIPPED order returns its stock to the
    // warehouse. A DELIVERED order can no longer be cancelled this way.
    @Transactional
    public CustomerOrder cancelOrder(Long id) {
        CustomerOrder order = getOrderById(id);
        if (order.getStatus() == Status.DELIVERED) {
            throw new IllegalArgumentException("A delivered order cannot be cancelled");
        }
        if (order.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("This order is already cancelled");
        }

        for (OrderItem item : order.getItems()) {
            inventoryService.adjustOrCreateQuantity(
                    item.getProduct().getId(), order.getWarehouse().getId(), item.getQuantity());
        }

        order.setStatus(Status.CANCELLED);
        return customerOrderRepository.save(order);
    }
}