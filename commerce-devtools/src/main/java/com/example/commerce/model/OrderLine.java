package com.example.commerce.model;

public record OrderLine(String sku, String name, int qty, Money unitPrice, Money lineTotal) { }
