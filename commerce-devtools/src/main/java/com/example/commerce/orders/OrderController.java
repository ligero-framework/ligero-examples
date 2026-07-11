package com.example.commerce.orders;

import com.example.commerce.model.PlaceOrderRequest;
import com.ligero.Ligero;
import com.ligero.beans.stereotype.Controller;

@Controller
public class OrderController {

    private final OrderService orders;

    public OrderController(OrderService orders) { this.orders = orders; }

    public void register(Ligero app) {
        app.group("/api/orders", api -> {
            api.post("", ctx -> ctx.status(201).json(orders.place(ctx.body(PlaceOrderRequest.class))));
            api.get("/{id}", ctx -> ctx.json(orders.get(ctx.pathParam("id"))));
            api.get("", ctx -> ctx.json(orders.forCustomer(Long.parseLong(ctx.queryParam("customerId")))));
        });
    }
}
