package com.example.orders.dto;

import com.example.orders.domain.Order;
import com.example.orders.domain.OrderItem;
import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String status,
        BigDecimal subtotal,
        BigDecimal appliedDiscountRate,
        BigDecimal total,
        List<LineItemResponse> items
) {
    public record LineItemResponse(String productName, int quantity, BigDecimal unitPrice) {
    }

    public static OrderResponse from(Order order) {
        List<LineItemResponse> lineItems = order.getItems().stream()
                .map(OrderResponse::toLineItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getStatus().name(),
                order.subtotal().asBigDecimal(),
                order.getAppliedDiscountRate(),
                order.total().asBigDecimal(),
                lineItems
        );
    }

    private static LineItemResponse toLineItemResponse(OrderItem item) {
        return new LineItemResponse(item.getProductName(), item.getQuantity(), item.getUnitPrice().asBigDecimal());
    }
}
