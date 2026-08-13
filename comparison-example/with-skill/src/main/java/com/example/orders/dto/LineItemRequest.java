package com.example.orders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record LineItemRequest(
        @NotBlank String productName,
        @Positive int quantity,
        @Positive java.math.BigDecimal unitPrice
) {
}
