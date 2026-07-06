package com.example.layered.repository;

import com.example.layered.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Implementación en memoria (perfil por defecto, arranque instantáneo). */
public final class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public InMemoryProductRepository() {
        save(new Product(null, "Teclado", new BigDecimal("29.90")));
        save(new Product(null, "Mouse", new BigDecimal("14.50")));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Product save(Product product) {
        long id = product.id() != null ? product.id() : ids.incrementAndGet();
        Product saved = new Product(id, product.name(), product.price());
        store.put(id, saved);
        return saved;
    }

    @Override
    public void deleteById(long id) {
        store.remove(id);
    }
}
