package com.example.commerce.orders;

import com.example.commerce.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> find(String id);
    List<Order> byCustomer(long customerId);
}
