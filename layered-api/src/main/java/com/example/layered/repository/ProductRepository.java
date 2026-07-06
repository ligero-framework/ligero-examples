package com.example.layered.repository;

import com.example.layered.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * Capa repository (interfaz): el service depende de esta abstracción, nunca
 * de una implementación concreta — igual que un repositorio de Spring Data,
 * pero explícito.
 */
public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(long id);

    Product save(Product product);

    void deleteById(long id);
}
