package com.example.commerce.orders;

import com.example.commerce.catalog.CatalogService;
import com.example.commerce.catalog.InventoryService;
import com.example.commerce.catalog.PricingService;
import com.example.commerce.customers.CustomerService;
import com.example.commerce.model.CatalogItem;
import com.example.commerce.model.Customer;
import com.example.commerce.model.LineItem;
import com.example.commerce.model.Money;
import com.example.commerce.model.Order;
import com.example.commerce.model.OrderLine;
import com.example.commerce.model.PlaceOrderRequest;
import com.example.commerce.model.Quote;
import com.ligero.beans.stereotype.Service;
import com.ligero.http.BadRequestException;
import com.ligero.http.NotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The deep node in the graph: placing an order fans out to the customer,
 * catalogue (+ inventory), pricing and inventory-reservation services, then
 * saves and notifies — so its trace lights up most of the bean graph.
 */
@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orders;
    private final CustomerService customers;
    private final CatalogService catalog;
    private final PricingService pricing;
    private final InventoryService inventory;
    private final NotificationService notifications;

    public DefaultOrderService(OrderRepository orders, CustomerService customers, CatalogService catalog,
                               PricingService pricing, InventoryService inventory, NotificationService notifications) {
        this.orders = orders;
        this.customers = customers;
        this.catalog = catalog;
        this.pricing = pricing;
        this.inventory = inventory;
        this.notifications = notifications;
    }

    @Override
    public Order place(PlaceOrderRequest request) {
        Customer customer = customers.get(request.customerId());
        if (request.items() == null || request.items().isEmpty()) {
            throw new BadRequestException("An order needs at least one item");
        }
        List<OrderLine> lines = new ArrayList<>();
        Money total = Money.usd(0);
        for (LineItem item : request.items()) {
            CatalogItem product = catalog.product(item.sku());        // -> ProductRepository + InventoryService
            Quote quote = pricing.quote(item.sku(), item.qty());      // -> ProductRepository
            if (!inventory.reserve(item.sku(), item.qty())) {         // -> InventoryRepository
                throw new BadRequestException("Not enough stock for " + item.sku()
                    + " (have " + product.available() + ")");
            }
            lines.add(new OrderLine(item.sku(), product.product().name(), item.qty(),
                quote.unitPrice(), quote.total()));
            total = total.plus(quote.total());
        }
        Order saved = orders.save(new Order(null, customer.id(), customer.name(),
            lines, total, "CONFIRMED", Instant.now()));
        notifications.orderConfirmed(saved.id(), customer.email());   // -> NotificationService
        return saved;
    }

    @Override
    public Order get(String id) {
        return orders.find(id).orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }

    @Override
    public List<Order> forCustomer(long customerId) {
        customers.get(customerId); // 404 if the customer doesn't exist
        return orders.byCustomer(customerId);
    }
}
