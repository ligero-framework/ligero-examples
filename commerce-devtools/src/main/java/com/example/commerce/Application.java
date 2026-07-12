package com.example.commerce;

import com.example.commerce.catalog.CatalogModule;
import com.example.commerce.customers.CustomersModule;
import com.example.commerce.orders.OrdersModule;
import com.ligero.Ligero;
import com.ligero.LigeroModule;
import com.ligero.Modules;
import com.ligero.beans.Beans;
import com.ligero.devtools.Devtools;
import com.ligero.middleware.RequestLoggingMiddleware;

/**
 * A small e-commerce backend that shows Ligero devtools on a realistic,
 * multi-feature dependency graph. Three modules (catalogue, customers, orders)
 * wire ~11 interface-typed beans with cross-module dependencies; the
 * {@code OrderService} orchestrates most of them, so placing an order lights up
 * a deep path in the graph.
 *
 * <p>Run: {@code ./gradlew :commerce-devtools:run} then open
 * <a href="http://localhost:8080/ligero/dev">/ligero/dev</a>.</p>
 */
public final class Application {

    public static void main(String[] args) throws Exception {
        Ligero app = create();
        // Ligero logs the startup line and devtools its /ligero/dev mount,
        // so main() stays free of println boilerplate.
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    public static Ligero create() {
        Ligero app = Ligero.create(8080);
        app.use(new RequestLoggingMiddleware());

        // Visual debugger at /ligero/dev (LIGERO_DEVTOOLS=false disables it).
        Devtools devtools = Devtools.create();
        Beans beans = Modules.install(app, devtools.recorder(), modules());
        devtools.install(app, beans);

        return app;
    }

    static LigeroModule[] modules() {
        return new LigeroModule[] {
            new CatalogModule(),
            new CustomersModule(),
            new OrdersModule(),
        };
    }
}
