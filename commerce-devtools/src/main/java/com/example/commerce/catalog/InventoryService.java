package com.example.commerce.catalog;

/** Availability and reservations — business layer over the inventory repo. */
public interface InventoryService {
    int availability(String sku);
    boolean reserve(String sku, int qty);
}
