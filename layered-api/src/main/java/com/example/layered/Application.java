package com.example.layered;

import com.example.layered.controller.ProductController;
import com.example.layered.repository.InMemoryProductRepository;
import com.example.layered.repository.JdbcProductRepository;
import com.example.layered.repository.ProductRepository;
import com.example.layered.service.ProductService;
import com.ligero.Ligero;
import com.ligero.middleware.HealthMiddleware;
import com.ligero.middleware.RequestLoggingMiddleware;

import javax.sql.DataSource;

/**
 * Composition root: aquí se resuelve TODA la inyección de dependencias.
 * Sin contenedor, sin reflexión — el grafo de objetos se arma a mano en
 * ~10 líneas y el compilador lo verifica. Para servicios que un middleware
 * o handler suelto necesite, además está app.register(...) / ctx.get(...).
 */
public final class Application {

    public static void main(String[] args) throws Exception {
        long startNanos = System.nanoTime();

        Ligero app = create();
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        long millis = (System.nanoTime() - startNanos) / 1_000_000;
        System.out.println("layered-api started in " + millis + " ms -> http://localhost:" + app.port());
    }

    public static Ligero create() {
        // --- wiring de dependencias (capa por capa, de abajo hacia arriba) ---
        ProductRepository repository = repository();
        ProductService service = new ProductService(repository);
        ProductController controller = new ProductController(service);

        Ligero app = Ligero.create(8080);
        app.use(new RequestLoggingMiddleware());
        app.use(HealthMiddleware.builder()
            .check("repository", () -> !repository.findAll().isEmpty() || true)
            .build());
        app.register(ProductRepository.class, repository); // disponible vía ctx.get(...)

        controller.register(app);
        return app;
    }

    /** Perfil por implementación: DB_URL presente -> PostgreSQL; si no, memoria. */
    private static ProductRepository repository() {
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            System.out.println("DB_URL not set -> using InMemoryProductRepository");
            return new InMemoryProductRepository();
        }
        var ds = new org.postgresql.ds.PGSimpleDataSource();
        ds.setUrl(url);
        ds.setUser(System.getenv().getOrDefault("DB_USER", "app"));
        ds.setPassword(System.getenv().getOrDefault("DB_PASSWORD", "app"));
        System.out.println("DB_URL set -> using JdbcProductRepository");
        return new JdbcProductRepository((DataSource) ds);
    }
}
