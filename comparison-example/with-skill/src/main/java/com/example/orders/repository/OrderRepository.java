package com.example.orders.repository;

import com.example.orders.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Derived query method: parameterized by Spring Data, no manual JPQL
    // string concatenation, no injection surface.
    @EntityGraph(attributePaths = "items")
    List<Order> findByCustomerName(String customerName);
}
