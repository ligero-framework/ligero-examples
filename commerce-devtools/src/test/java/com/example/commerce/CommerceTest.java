package com.example.commerce;

import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommerceTest {

    @Test
    void placesAnOrderThroughEveryLayer() {
        try (LigeroTest test = LigeroTest.start(Application.create())) {
            // catalogue + availability
            LigeroTest.TestResponse catalog = test.get("/api/catalog/products").execute();
            assertEquals(200, catalog.status());
            assertTrue(catalog.body().contains("KB-1"));

            // pricing with a volume discount
            LigeroTest.TestResponse quote = test.get("/api/catalog/products/KB-1/quote?qty=10").execute();
            assertEquals(200, quote.status());
            assertTrue(quote.body().contains("discount"));

            // place an order — the deep path across services and repositories
            LigeroTest.TestResponse placed = test.post("/api/orders")
                .body("{\"customerId\":1,\"items\":[{\"sku\":\"KB-1\",\"qty\":2},{\"sku\":\"CB-3\",\"qty\":3}]}")
                .execute();
            assertEquals(201, placed.status());
            assertTrue(placed.body().contains("CONFIRMED"));
            assertTrue(placed.body().contains("Ada Lovelace"));

            // recommendations for the customer
            LigeroTest.TestResponse recs = test.get("/api/customers/1/recommendations").execute();
            assertEquals(200, recs.status());
        }
    }
}
