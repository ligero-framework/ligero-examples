package com.example.commerce.customers;

import com.example.commerce.model.Customer;
import com.ligero.beans.stereotype.Service;
import com.ligero.http.NotFoundException;

@Service
public class DefaultCustomerService implements CustomerService {

    private final CustomerRepository customers;

    public DefaultCustomerService(CustomerRepository customers) { this.customers = customers; }

    @Override
    public Customer get(long id) {
        return customers.find(id).orElseThrow(() -> new NotFoundException("Customer " + id + " not found"));
    }
}
