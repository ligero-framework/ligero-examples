package com.example.layered.service;

import com.example.layered.domain.Product;
import com.example.layered.repository.ProductRepository;
import com.ligero.http.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Capa service: lógica de negocio. La dependencia llega por constructor
 * (inyección explícita — sin reflexión ni anotaciones); testear con un
 * repositorio fake es trivial.
 */
public final class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> list() {
        return repository.findAll();
    }

    public Product get(long id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    public Product create(String name, BigDecimal price) {
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must not be negative");
        }
        return repository.save(new Product(null, name, price));
    }

    public void delete(long id) {
        get(id); // 404 si no existe
        repository.deleteById(id);
    }
}
