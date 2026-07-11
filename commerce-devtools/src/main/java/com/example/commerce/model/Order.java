package com.example.commerce.model;

import java.time.Instant;
import java.util.List;

public record Order(String id, long customerId, String customerName,
                    List<OrderLine> lines, Money total, String status, Instant placedAt) { }
