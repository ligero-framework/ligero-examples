package com.example.commerce.model;

/** A product plus its live availability. */
public record CatalogItem(Product product, int available, boolean inStock) { }
