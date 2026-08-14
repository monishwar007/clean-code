package com.example.orders.controller;

import com.example.orders.domain.Order;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.LineItemRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * The controller is pure delegation (request/response mapping only, per
 * architecture.md), so a plain unit test with a mocked service is enough
 * here. A real project would add a @WebMvcTest slice test for HTTP-level
 * concerns (status codes, JSON shape, validation error responses).
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController controller;

    @Test
    void getOrderReturnsMappedResponse() {
        Order order = new Order("Jane Doe", "jane@example.com");
        when(orderService.getOrder(1L)).thenReturn(order);

        OrderResponse response = controller.getOrder(1L);

        assertEquals("Jane Doe", response.customerName());
    }

    @Test
    void createOrderReturnsHttp201() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Jane Doe",
                "jane@example.com",
                List.of(new LineItemRequest("Widget", 1, new BigDecimal("50")))
        );
        Order order = new Order("Jane Doe", "jane@example.com");
        when(orderService.createOrder(request)).thenReturn(order);

        ResponseEntity<OrderResponse> response = controller.createOrder(request);

        assertEquals(201, response.getStatusCodeValue());
    }
}
