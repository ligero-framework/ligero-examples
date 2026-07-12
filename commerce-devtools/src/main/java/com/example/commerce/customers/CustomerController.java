package com.example.commerce.customers;

import com.ligero.Ligero;
import com.ligero.beans.stereotype.Controller;

@Controller
public class CustomerController {

    private final CustomerService customers;

    public CustomerController(CustomerService customers) { this.customers = customers; }

    public void register(Ligero app) {
        app.get("/api/customers/{id}", ctx -> ctx.json(customers.get(ctx.pathParamAsLong("id"))));
    }
}
