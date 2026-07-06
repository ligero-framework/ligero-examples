package com.example.layeredbeans.controller;

import com.example.layeredbeans.domain.Product;
import com.example.layeredbeans.service.ProductService;
import com.ligero.Ligero;
import com.ligero.beans.stereotype.Controller;

import java.math.BigDecimal;

/**
 * Capa controller: solo HTTP (parseo, validación, códigos de estado).
 * Se liga como clase concreta — sus rutas son lambdas, así que lo que se
 * traza por request son las llamadas service -> repository que dispara.
 */
@Controller
public final class ProductController {

    record CreateProductRequest(String name, BigDecimal price) {
    }

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    public void register(Ligero app) {
        app.group("/api/products", api -> {
            api.get("", ctx -> ctx.json(service.list()));

            api.get("/{id}", ctx -> ctx.json(service.get(ctx.pathParamAsLong("id"))));

            api.post("", ctx -> {
                CreateProductRequest request = ctx.bodyValidator(CreateProductRequest.class)
                    .check(r -> r.name() != null && !r.name().isBlank(), "name is required")
                    .check(r -> r.price() != null, "price is required")
                    .get();
                Product created = service.create(request.name(), request.price());
                ctx.status(201).header("Location", "/api/products/" + created.id()).json(created);
            });

            api.delete("/{id}", ctx -> {
                service.delete(ctx.pathParamAsLong("id"));
                ctx.status(204).res().end();
            });
        });
    }
}
