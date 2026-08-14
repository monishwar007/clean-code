package com.example.orders.service;

import com.example.orders.domain.Order;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.LineItemRequest;
import com.example.orders.exception.OrderNotFoundException;
import com.example.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPolicy orderPolicy;

    @InjectMocks
    private OrderService orderService;

    @Test
    void throwsOrderNotFoundExceptionWhenMissing() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(99L));
    }

    @Test
    void returnsOrderWhenFound() {
        Order order = new Order("Jane Doe", "jane@example.com");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(1L);

        assertEquals("Jane Doe", result.getCustomerName());
    }

    @Test
    void createOrderAppliesPricingPolicyBeforeSaving() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Jane Doe",
                "jane@example.com",
                List.of(new LineItemRequest("Widget", 2, new BigDecimal("100")))
        );
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(request);

        // Policy must run before the order is persisted, not after.
        verify(orderPolicy).price(result);
        assertEquals("Jane Doe", result.getCustomerName());
    }
}
