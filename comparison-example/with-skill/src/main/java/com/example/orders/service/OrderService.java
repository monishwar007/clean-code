package com.example.orders.service;

import com.example.orders.domain.Money;
import com.example.orders.domain.Order;
import com.example.orders.domain.OrderItem;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.LineItemRequest;
import com.example.orders.exception.OrderNotFoundException;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderPolicy orderPolicy;

    public OrderService(OrderRepository orderRepository, OrderPolicy orderPolicy) {
        this.orderRepository = orderRepository;
        this.orderPolicy = orderPolicy;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(request.customerName(), request.customerEmail());
        for (LineItemRequest itemRequest : request.items()) {
            order.addItem(new OrderItem(
                    itemRequest.productName(),
                    itemRequest.quantity(),
                    Money.of(itemRequest.unitPrice())
            ));
        }
        orderPolicy.price(order);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Order> findByCustomer(String customerName) {
        return orderRepository.findByCustomerName(customerName);
    }
}
