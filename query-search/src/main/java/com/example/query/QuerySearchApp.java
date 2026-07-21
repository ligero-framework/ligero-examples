package com.example.query;

import com.ligero.Ligero;

import java.util.List;
import java.util.Map;

/**
 * Demonstrates the HTTP QUERY method: a safe, idempotent read that carries a
 * structured filter in the request body (too rich for a query string).
 *
 * <pre>
 * curl -X QUERY localhost:8080/search -H 'Content-Type: application/json' \
 *   -d '{"text":"laptop","maxPrice":1000}'
 * </pre>
 */
public final class QuerySearchApp {

    public record Product(String name, String category, int price) { }

    public record Filter(String text, String category, Integer maxPrice) { }

    private static final List<Product> CATALOG = List.of(
        new Product("Gaming Laptop", "computers", 1499),
        new Product("Office Laptop", "computers", 899),
        new Product("Mechanical Keyboard", "accessories", 120),
        new Product("USB-C Hub", "accessories", 45),
        new Product("4K Monitor", "computers", 640));

    public static void main(String[] args) throws Exception {
        Ligero app = Ligero.create(8080);

        app.query("/search", ctx -> {
            Filter f = ctx.body(Filter.class);
            List<Product> results = CATALOG.stream()
                .filter(p -> f.text() == null
                    || p.name().toLowerCase().contains(f.text().toLowerCase()))
                .filter(p -> f.category() == null || p.category().equals(f.category()))
                .filter(p -> f.maxPrice() == null || p.price() <= f.maxPrice())
                .toList();
            ctx.json(Map.of("count", results.size(), "results", results));
        });

        app.start();
    }
}
