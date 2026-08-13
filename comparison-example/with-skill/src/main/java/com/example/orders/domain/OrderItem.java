package com.example.orders.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    protected OrderItem() {
        // JPA only
    }

    public OrderItem(String productName, int quantity, Money unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice.asBigDecimal();
    }

    public Money lineTotal() {
        return Money.of(unitPrice).multiply(quantity);
    }

    void assignTo(Order order) {
        this.order = order;
    }

    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public Money getUnitPrice() { return Money.of(unitPrice); }
}
