package com.hcl.inventory.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

    public enum Status { PENDING, ORDERED, RECEIVED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Added beyond the original data model: receiving stock has to go
    // somewhere, so the PO needs a target warehouse.
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime orderDate;

    @NotEmpty
    @Valid
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PurchaseOrderItem> items = new ArrayList<>();

    // Transient: used only to receive ids from incoming JSON.
    @Transient
    private Long supplierId;

    @Transient
    private Long warehouseId;

    public void addItem(PurchaseOrderItem item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }
}