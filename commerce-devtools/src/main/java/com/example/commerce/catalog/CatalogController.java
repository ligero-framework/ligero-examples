package com.example.commerce.catalog;

import com.ligero.Ligero;
import com.ligero.beans.stereotype.Controller;

/** HTTP for the catalogue and pricing. */
@Controller
public class CatalogController {

    private final CatalogService catalog;
    private final PricingService pricing;

    public CatalogController(CatalogService catalog, PricingService pricing) {
        this.catalog = catalog;
        this.pricing = pricing;
    }

    public void register(Ligero app) {
        app.group("/api/catalog", api -> {
            api.get("/products", ctx -> ctx.json(catalog.catalog()));
            api.get("/products/{sku}", ctx -> ctx.json(catalog.product(ctx.pathParam("sku"))));
            api.get("/products/{sku}/quote", ctx -> {
                int qty = ctx.queryParam("qty") == null ? 1 : Integer.parseInt(ctx.queryParam("qty"));
                ctx.json(pricing.quote(ctx.pathParam("sku"), qty));
            });
        });
    }
}
