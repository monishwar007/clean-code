package com.example.orders.service;

import com.example.orders.domain.Money;
import com.example.orders.domain.Order;
import com.example.orders.domain.OrderItem;
import com.example.orders.domain.ThresholdDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderPolicyTest {

    private final OrderPolicy policy = new OrderPolicy(
            new ThresholdDiscountStrategy(Money.of("1000"), new BigDecimal("0.10"))
    );

    @Test
    void appliesTenPercentDiscountAboveThreshold() {
        Order order = new Order("Jane Doe", "jane@example.com");
        order.addItem(new OrderItem("Widget", 3, Money.of("500"))); // subtotal 1500

        policy.price(order);

        assertEquals(Money.of("1350.00"), order.total());
    }

    @Test
    void appliesNoDiscountAtOrBelowThreshold() {
        Order order = new Order("Jane Doe", "jane@example.com");
        order.addItem(new OrderItem("Widget", 2, Money.of("500"))); // subtotal 1000

        policy.price(order);

        assertEquals(Money.of("1000.00"), order.total());
    }
}
