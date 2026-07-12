package com.example.commerce.orders;

import com.example.commerce.model.Order;
import com.ligero.beans.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1000);

    @Override
    public Order save(Order order) {
        String id = order.id() != null ? order.id() : "ORD-" + seq.incrementAndGet();
        Order stored = new Order(id, order.customerId(), order.customerName(),
            order.lines(), order.total(), order.status(), order.placedAt());
        orders.put(id, stored);
        return stored;
    }

    @Override
    public Optional<Order> find(String id) { return Optional.ofNullable(orders.get(id)); }

    @Override
    public List<Order> byCustomer(long customerId) {
        return orders.values().stream().filter(o -> o.customerId() == customerId).toList();
    }
}
