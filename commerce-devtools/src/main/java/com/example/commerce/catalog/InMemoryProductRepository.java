package com.example.commerce.catalog;

import com.example.commerce.model.Money;
import com.example.commerce.model.Product;
import com.ligero.beans.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = List.of(
        new Product("KB-1", "Mechanical keyboard", "peripherals", Money.usd(12900)),
        new Product("MS-2", "Trackball mouse",     "peripherals", Money.usd(4900)),
        new Product("CB-3", "USB-C cable",         "accessories", Money.usd(1500)),
        new Product("MN-4", "27\" monitor",        "displays",    Money.usd(28900)),
        new Product("HS-5", "Noise-cancel headset","audio",       Money.usd(19900)));

    @Override
    public List<Product> all() { return products; }

    @Override
    public Optional<Product> findBySku(String sku) {
        return products.stream().filter(p -> p.sku().equals(sku)).findFirst();
    }
}
