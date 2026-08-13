package com.example.orders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Everything lives here: persistence, business rules, HTTP mapping, error
// handling (or lack of it). No service layer, no DTOs, no tests.
@RestController
@RequestMapping("/orders")
public class OrderController {

    // Field injection instead of constructor injection.
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    // POST /orders — takes the JPA entity directly as the request body.
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // Business rule buried in the controller, with magic numbers and
        // deep nesting instead of guard clauses.
        double total = 0;
        if (order.getItems() != null) {
            if (order.getItems().size() > 0) {
                for (OrderItem item : order.getItems()) {
                    if (item.getQuantity() > 0) {
                        total = total + (item.getPrice() * item.getQuantity());
                        item.setOrder(order);
                    }
                }
            }
        }
        // 10% discount over 1000, hardcoded here, duplicated wherever
        // total needs recalculating (e.g. after an item is added later).
        if (total > 1000) {
            total = total - (total * 0.1);
        }
        order.setTotal(total);
        order.setStatus("CREATED");
        return orderRepository.save(order); // returns the entity straight back as JSON
    }

    // GET /orders/{id} — no 404 handling; a missing order just returns null
    // and Spring serializes it as an empty 200 body.
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // GET /orders/search?name=... — string-concatenated JPQL: a textbook
    // SQL/JPQL injection vulnerability, and a full table scan since there's
    // no parameterization or index-friendly query shape.
    @GetMapping("/search")
    public List<Order> search(@RequestParam String name) {
        String jpql = "SELECT o FROM Order o WHERE o.customerName = '" + name + "'";
        Query query = entityManager.createQuery(jpql);
        return query.getResultList();
    }

    // PUT /orders/{id}/status — swallows any error silently.
    @PutMapping("/{id}/status")
    public Map<String, String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Map<String, String> result = new HashMap<>();
        try {
            Order order = orderRepository.findById(id).orElse(null);
            order.setStatus(status);
            orderRepository.save(order);
            result.put("result", "ok");
        } catch (Exception e) {
            // swallowed — caller has no idea the order didn't exist
            result.put("result", "error");
        }
        return result;
    }
}
