package com.example.layeredbeans.service;

import com.example.layeredbeans.domain.Product;
import com.example.layeredbeans.repository.ProductRepository;
import com.ligero.beans.stereotype.Service;
import com.ligero.http.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

/**
 * La dependencia llega por constructor, declarada en el composition root
 * (Application.wire). @Service es metadata pura: colorea el nodo en el
 * dashboard de devtools, nunca dispara scanning ni reflexión.
 */
@Service
public final class DefaultProductService implements ProductService {

    private final ProductRepository repository;

    public DefaultProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> list() {
        return repository.findAll();
    }

    @Override
    public Product get(long id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    @Override
    public Product create(String name, BigDecimal price) {
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must not be negative");
        }
        return repository.save(new Product(null, name, price));
    }

    @Override
    public void delete(long id) {
        get(id); // 404 si no existe
        repository.deleteById(id);
    }
}
