package com.example.orders.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable value object for currency amounts. Eliminates primitive
 * obsession (raw double/BigDecimal scattered through the codebase) and
 * centralizes rounding/scale rules in one place.
 */
public final class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money cannot be negative: " + amount);
        }
        return new Money(amount);
    }

    public static Money of(String amount) {
        return of(new BigDecimal(amount));
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public Money percentageOff(BigDecimal rate) {
        BigDecimal discount = this.amount.multiply(rate);
        return new Money(this.amount.subtract(discount));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public BigDecimal asBigDecimal() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
