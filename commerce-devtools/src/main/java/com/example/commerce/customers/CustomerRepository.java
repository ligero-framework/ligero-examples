package com.example.commerce.customers;

import com.example.commerce.model.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> find(long id);
}
