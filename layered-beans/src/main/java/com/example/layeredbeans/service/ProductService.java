package com.example.layeredbeans.service;

import com.example.layeredbeans.domain.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Capa service (interfaz): lógica de negocio detrás de una abstracción.
 * Ligada como interfaz para poder intercambiarla en tests y para que
 * devtools trace sus llamadas.
 */
public interface ProductService {

    List<Product> list();

    Product get(long id);

    Product create(String name, BigDecimal price);

    void delete(long id);
}
