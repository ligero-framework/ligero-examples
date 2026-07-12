package com.example.commerce.customers;

import com.example.commerce.model.Customer;
import com.ligero.beans.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<Long, Customer> customers = Map.of(
        1L, new Customer(1, "Ada Lovelace", "ada@example.com", "gold"),
        2L, new Customer(2, "Grace Hopper", "grace@example.com", "standard"));

    @Override
    public Optional<Customer> find(long id) { return Optional.ofNullable(customers.get(id)); }
}
