package com.example.commerce.catalog;

import com.ligero.beans.stereotype.Service;

@Service
public class DefaultInventoryService implements InventoryService {

    private final InventoryRepository inventory;

    public DefaultInventoryService(InventoryRepository inventory) { this.inventory = inventory; }

    @Override
    public int availability(String sku) { return inventory.available(sku); }

    @Override
    public boolean reserve(String sku, int qty) { return inventory.reserve(sku, qty); }
}
