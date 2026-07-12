package com.example.commerce.model;

import java.util.List;

public record PlaceOrderRequest(long customerId, List<LineItem> items) { }
