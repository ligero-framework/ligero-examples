package com.example.layeredbeans;

import com.ligero.Modules;
import com.ligero.beans.Beans;
import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayeredBeansTest {

    @Test
    void crudFlowThroughAllLayers() {
        // El mismo módulo que producción (sin DB_URL usa el repo en memoria).
        try (LigeroTest test = LigeroTest.create(app ->
                Modules.install(app, new ProductsModule()))) {
            assertTrue(test.get("/api/products").execute().body().contains("Teclado"));

            var created = test.post("/api/products").json("{\"name\":\"Monitor\",\"price\":199.99}").execute();
            assertEquals(201, created.status());

            var invalid = test.post("/api/products").json("{\"price\":1}").execute();
            assertEquals(400, invalid.status());

            assertEquals(404, test.get("/api/products/999").execute().status());
        }
    }

    @Test
    void beansGraphDescribesTheLayers() {
        Beans beans = Modules.install(com.ligero.Ligero.create(), new ProductsModule());
        var stereotypes = beans.graph().nodes().stream()
            .map(node -> node.stereotype())
            .toList();
        assertTrue(stereotypes.containsAll(List.of("repository", "service", "controller")));
        assertEquals(2, beans.graph().edges().size()); // controller -> service -> repository
    }
}
