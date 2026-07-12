package com.example.commerce.catalog;

import com.ligero.beans.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInventoryRepository implements InventoryRepository {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>(Map.of(
        "KB-1", 40, "MS-2", 12, "CB-3", 200, "MN-4", 7, "HS-5", 0));

    @Override
    public int available(String sku) { return stock.getOrDefault(sku, 0); }

    @Override
    public boolean reserve(String sku, int qty) {
        return stock.merge(sku, 0, Integer::sum) >= qty
            && stock.computeIfPresent(sku, (k, v) -> v >= qty ? v - qty : v) != null
            && available(sku) >= 0;
    }
}
