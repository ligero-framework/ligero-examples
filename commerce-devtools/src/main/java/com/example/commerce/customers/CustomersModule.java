package com.example.commerce.customers;

import com.ligero.Ligero;
import com.ligero.LigeroModule;
import com.ligero.beans.Beans;

public final class CustomersModule implements LigeroModule {

    @Override
    public void beans(Beans.Builder b) {
        b.bind(CustomerRepository.class, x -> new InMemoryCustomerRepository());
        b.bind(CustomerService.class,    x -> new DefaultCustomerService(x.get(CustomerRepository.class)));
        b.bind(CustomerController.class, x -> new CustomerController(x.get(CustomerService.class)));
    }

    @Override
    public void routes(Ligero app, Beans beans) {
        beans.get(CustomerController.class).register(app);
    }
}
