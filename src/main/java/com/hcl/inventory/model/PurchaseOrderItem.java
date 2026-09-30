package com.hcl.inventory.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    @JsonBackReference
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private BigDecimal unitCost;

    // Transient: used only to receive the product's id from incoming JSON,
    // e.g. {"productId": 2, "quantity": 10, "unitCost": 50.00}
    @Transient
    private Long productId;
}