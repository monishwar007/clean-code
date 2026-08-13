package com.example.orders.domain;

import java.math.BigDecimal;

public class ThresholdDiscountStrategy implements DiscountStrategy {

    private final Money threshold;
    private final BigDecimal rate;

    public ThresholdDiscountStrategy(Money threshold, BigDecimal rate) {
        this.threshold = threshold;
        this.rate = rate;
    }

    @Override
    public BigDecimal discountRateFor(Money subtotal) {
        return subtotal.isGreaterThan(threshold) ? rate : BigDecimal.ZERO;
    }
}
