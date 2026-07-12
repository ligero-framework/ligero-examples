package com.example.commerce.model;

/** A requested line in an order: which SKU and how many. */
public record LineItem(String sku, int qty) { }
