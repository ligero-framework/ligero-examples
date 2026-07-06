package com.example.layered;

import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayeredApiTest {

    @Test
    void crudFlowThroughAllLayers() {
        // misma composición que producción, pero en puerto efímero
        try (LigeroTest test = LigeroTest.create(app -> {
            var repo = new com.example.layered.repository.InMemoryProductRepository();
            var controller = new com.example.layered.controller.ProductController(
                new com.example.layered.service.ProductService(repo));
            controller.register(app);
        })) {
            assertTrue(test.get("/api/products").execute().body().contains("Teclado"));

            var created = test.post("/api/products").json("{\"name\":\"Monitor\",\"price\":199.99}").execute();
            assertEquals(201, created.status());

            var invalid = test.post("/api/products").json("{\"price\":1}").execute();
            assertEquals(400, invalid.status());

            assertEquals(404, test.get("/api/products/999").execute().status());
        }
    }
}
