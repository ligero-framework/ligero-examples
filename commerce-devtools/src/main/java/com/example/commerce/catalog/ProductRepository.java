package com.example.commerce.catalog;

import com.example.commerce.model.Product;

import java.util.List;
import java.util.Optional;

/** Data access for products. An interface, so devtools can trace it. */
public interface ProductRepository {
    List<Product> all();
    Optional<Product> findBySku(String sku);
}
