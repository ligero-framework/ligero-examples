package com.example.commerce.catalog;

import com.ligero.Ligero;
import com.ligero.LigeroModule;
import com.ligero.beans.Beans;

/** The catalogue slice: products, inventory, pricing — beans and routes. */
public final class CatalogModule implements LigeroModule {

    @Override
    public void beans(Beans.Builder b) {
        b.bind(ProductRepository.class,   x -> new InMemoryProductRepository());
        b.bind(InventoryRepository.class, x -> new InMemoryInventoryRepository());
        b.bind(InventoryService.class,    x -> new DefaultInventoryService(x.get(InventoryRepository.class)));
        b.bind(PricingService.class,      x -> new DefaultPricingService(x.get(ProductRepository.class)));
        b.bind(CatalogService.class,      x -> new DefaultCatalogService(x.get(ProductRepository.class), x.get(InventoryService.class)));
        b.bind(CatalogController.class,   x -> new CatalogController(x.get(CatalogService.class), x.get(PricingService.class)));
    }

    @Override
    public void routes(Ligero app, Beans beans) {
        beans.get(CatalogController.class).register(app);
    }
}
