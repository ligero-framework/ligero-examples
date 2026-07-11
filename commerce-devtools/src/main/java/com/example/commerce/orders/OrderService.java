package com.example.commerce.orders;

import com.example.commerce.model.Order;
import com.example.commerce.model.PlaceOrderRequest;

import java.util.List;

/** Orchestrates placing and reading orders across catalogue, pricing and inventory. */
public interface OrderService {
    Order place(PlaceOrderRequest request);
    Order get(String id);
    List<Order> forCustomer(long customerId);
}
