package com.hcl.inventory.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    public enum Status { PLACED, SHIPPED, DELIVERED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String customerName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.PLACED;

    private LocalDateTime orderDate;

    // Warehouse the order is fulfilled from.
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    // Sales/staff user who processed the order.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotEmpty
    @Valid
    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    // Transient: used only to receive ids from incoming JSON.
    @Transient
    private Long warehouseId;

    @Transient
    private Long userId;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setCustomerOrder(this);
    }
}