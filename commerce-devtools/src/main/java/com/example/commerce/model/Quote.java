package com.example.commerce.model;

public record Quote(String sku, int qty, Money unitPrice, Money total, String note) { }
