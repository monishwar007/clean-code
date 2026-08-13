package com.example.orders.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal appliedDiscountRate = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<OrderItem> items = new ArrayList<>();

    protected Order() {
        // JPA only
    }

    public Order(String customerName, String customerEmail) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = OrderStatus.CREATED;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.assignTo(this);
    }

    public Money subtotal() {
        Money sum = Money.ZERO;
        for (OrderItem item : items) {
            sum = sum.add(item.lineTotal());
        }
        return sum;
    }

    /** Tell, don't ask: the order applies its own discount rule via an
     *  injected strategy, rather than exposing internals for a caller to
     *  compute the discount itself. */
    public void applyDiscount(DiscountStrategy strategy) {
        this.appliedDiscountRate = strategy.discountRateFor(subtotal());
    }

    public Money total() {
        return subtotal().percentageOff(appliedDiscountRate);
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getAppliedDiscountRate() { return appliedDiscountRate; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
}
