package com.example.orders.service;

import com.example.orders.domain.DiscountStrategy;
import com.example.orders.domain.Order;
import org.springframework.stereotype.Component;

/**
 * Single responsibility: decide and apply pricing rules to an order.
 * Knows nothing about persistence or HTTP — testable with a plain JUnit
 * test and no Spring context.
 */
@Component
public class OrderPolicy {

    private final DiscountStrategy discountStrategy;

    public OrderPolicy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public void price(Order order) {
        order.applyDiscount(discountStrategy);
    }
}
