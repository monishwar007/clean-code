package com.example.orders.domain;

import java.math.BigDecimal;

/**
 * Open/Closed in practice: new discount rules are added by writing a new
 * implementation, never by editing existing tested code or growing an
 * if/else chain in OrderPolicy.
 */
public interface DiscountStrategy {
    BigDecimal discountRateFor(Money subtotal);
}
