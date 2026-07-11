package com.example.commerce.catalog;

import com.example.commerce.model.Quote;

/** Prices a SKU for a quantity, applying volume discounts. */
public interface PricingService {
    Quote quote(String sku, int qty);
}
