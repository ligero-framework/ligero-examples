package com.example.commerce.catalog;

/** Data access for stock levels. */
public interface InventoryRepository {
    int available(String sku);
    boolean reserve(String sku, int qty);
}
