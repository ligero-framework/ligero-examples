package com.example.layeredbeans;

import com.example.layeredbeans.controller.ProductController;
import com.example.layeredbeans.repository.InMemoryProductRepository;
import com.example.layeredbeans.repository.JdbcProductRepository;
import com.example.layeredbeans.repository.ProductRepository;
import com.example.layeredbeans.service.DefaultProductService;
import com.example.layeredbeans.service.ProductService;
import com.ligero.Ligero;
import com.ligero.LigeroModule;
import com.ligero.beans.Beans;

import javax.sql.DataSource;

/**
 * Módulo de la feature "products": TODO el wiring de sus capas vive aquí —
 * una capa propia de clases, separada del startup (Application solo lista
 * módulos). Falta un binding o hay un ciclo -> error legible en el
 * arranque, nunca en mitad de un request.
 */
public final class ProductsModule implements LigeroModule {

    @Override
    public void beans(Beans.Builder builder) {
        builder
            .bind(ProductRepository.class, b -> repository())
            .bind(ProductService.class,    b -> new DefaultProductService(b.get(ProductRepository.class)))
            .bind(ProductController.class, b -> new ProductController(b.get(ProductService.class)));
    }

    @Override
    public void routes(Ligero app, Beans beans) {
        beans.get(ProductController.class).register(app);
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
