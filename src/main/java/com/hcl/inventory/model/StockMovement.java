package com.hcl.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    public enum MovementType { IN, OUT, TRANSFER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Only set for OUT and TRANSFER movements.
    @ManyToOne
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    // Only set for IN and TRANSFER movements.
    @ManyToOne
    @JoinColumn(name = "to_warehouse_id")
    private Warehouse toWarehouse;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MovementType type;

    private LocalDateTime timestamp;

    // Transient fields: used only to receive ids from incoming JSON, e.g.
    // {"productId": 1, "fromWarehouseId": 2, "toWarehouseId": 3,
    //  "quantity": 10, "type": "TRANSFER"}
    // The service layer reads these, resolves the real entities, and sets
    // them above.
    @Transient
    private Long productId;

    @Transient
    private Long fromWarehouseId;

    @Transient
    private Long toWarehouseId;
}