package com.example.layered.domain;

import java.math.BigDecimal;

/** Entidad de dominio: un record inmutable, sin anotaciones. */
public record Product(Long id, String name, BigDecimal price) {
}
