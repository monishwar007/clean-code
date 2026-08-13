package com.example.orders.service;

import com.example.orders.domain.DiscountStrategy;
import com.example.orders.domain.Money;
import com.example.orders.domain.ThresholdDiscountStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class PricingConfig {

    @Bean
    public DiscountStrategy discountStrategy() {
        return new ThresholdDiscountStrategy(Money.of("1000"), new BigDecimal("0.10"));
    }
}
