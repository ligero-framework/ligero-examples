package com.example.layeredbeans.repository;

import com.example.layeredbeans.domain.Product;
import com.ligero.beans.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC contra PostgreSQL (se activa cuando DB_URL está definida). */
@Repository
public final class JdbcProductRepository implements ProductRepository {

    private final DataSource dataSource;

    public JdbcProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Product> findAll() {
        try (Connection c = dataSource.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, name, price FROM products ORDER BY id")) {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(new Product(rs.getLong(1), rs.getString(2), rs.getBigDecimal(3)));
            }
            return products;
        } catch (Exception e) {
            throw new IllegalStateException("findAll failed", e);
        }
    }

    @Override
    public Optional<Product> findById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, price FROM products WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new Product(rs.getLong(1), rs.getString(2), rs.getBigDecimal(3)))
                    : Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException("findById failed", e);
        }
    }

    @Override
    public Product save(Product product) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO products(name, price) VALUES (?, ?) RETURNING id")) {
            ps.setString(1, product.name());
            ps.setBigDecimal(2, product.price());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Product(rs.getLong(1), product.name(), product.price());
            }
        } catch (Exception e) {
            throw new IllegalStateException("save failed", e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("delete failed", e);
        }
    }
}
