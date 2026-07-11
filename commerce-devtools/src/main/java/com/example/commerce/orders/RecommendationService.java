package com.example.commerce.orders;

import com.example.commerce.model.CatalogItem;

import java.util.List;

/** Suggests in-stock products, biased by what a customer already bought. */
public interface RecommendationService {
    List<CatalogItem> forCustomer(long customerId);
}
