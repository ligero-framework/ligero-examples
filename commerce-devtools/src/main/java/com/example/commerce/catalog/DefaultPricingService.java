package com.example.commerce.catalog;

import com.example.commerce.model.Money;
import com.example.commerce.model.Product;
import com.example.commerce.model.Quote;
import com.ligero.beans.stereotype.Service;
import com.ligero.http.NotFoundException;

@Service
public class DefaultPricingService implements PricingService {

    private final ProductRepository products;

    public DefaultPricingService(ProductRepository products) { this.products = products; }

    @Override
    public Quote quote(String sku, int qty) {
        Product p = products.findBySku(sku)
            .orElseThrow(() -> new NotFoundException("Unknown SKU " + sku));
        long unit = p.price().cents();
        String note = "list price";
        if (qty >= 10) { unit = Math.round(unit * 0.9); note = "10% volume discount"; }
        Money unitPrice = Money.usd(unit);
        return new Quote(sku, qty, unitPrice, unitPrice.times(qty), note);
    }
}
