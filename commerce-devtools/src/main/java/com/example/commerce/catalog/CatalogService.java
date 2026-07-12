package com.example.commerce.catalog;

import com.example.commerce.model.CatalogItem;

import java.util.List;

/** Product catalogue enriched with live availability. */
public interface CatalogService {
    List<CatalogItem> catalog();
    CatalogItem product(String sku);
}
