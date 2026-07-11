package com.example.commerce.model;

public record Product(String sku, String name, String category, Money price) { }
