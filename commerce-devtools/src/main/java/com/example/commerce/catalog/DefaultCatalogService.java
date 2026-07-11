package com.example.commerce.catalog;

import com.example.commerce.model.CatalogItem;
import com.example.commerce.model.Product;
import com.ligero.beans.stereotype.Service;
import com.ligero.http.NotFoundException;

import java.util.List;

@Service
public class DefaultCatalogService implements CatalogService {

    private final ProductRepository products;
    private final InventoryService inventory;

    public DefaultCatalogService(ProductRepository products, InventoryService inventory) {
        this.products = products;
        this.inventory = inventory;
    }

    @Override
    public List<CatalogItem> catalog() {
        return products.all().stream().map(this::withAvailability).toList();
    }

    @Override
    public CatalogItem product(String sku) {
        Product p = products.findBySku(sku)
            .orElseThrow(() -> new NotFoundException("Unknown SKU " + sku));
        return withAvailability(p);
    }

    private CatalogItem withAvailability(Product p) {
        int available = inventory.availability(p.sku());
        return new CatalogItem(p, available, available > 0);
    }
}
