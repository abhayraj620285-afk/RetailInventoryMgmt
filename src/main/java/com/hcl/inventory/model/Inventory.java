package com.hcl.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @NotNull
    @PositiveOrZero
    private Integer quantity;

    // Transient fields: used only to receive ids from incoming JSON
    // (e.g. {"productId": 1, "warehouseId": 2, "quantity": 50}) without
    // forcing clients to send nested product/warehouse objects. The service
    // layer reads these, looks up the real entities, and sets them above.
    @Transient
    private Long productId;

    @Transient
    private Long warehouseId;
}