package com.example.layeredbeans.repository;

import com.example.layeredbeans.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * Capa repository (interfaz). Además de mantener las capas intercambiables,
 * ligarla como interfaz permite que devtools espíe cada llamada con un
 * proxy dinámico y la muestre en la traza del request.
 */
public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(long id);

    Product save(Product product);

    void deleteById(long id);
}
