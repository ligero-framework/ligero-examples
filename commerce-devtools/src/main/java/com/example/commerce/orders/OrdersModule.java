package com.example.commerce.orders;

import com.example.commerce.catalog.CatalogService;
import com.example.commerce.catalog.InventoryService;
import com.example.commerce.catalog.PricingService;
import com.example.commerce.customers.CustomerService;
import com.ligero.Ligero;
import com.ligero.LigeroModule;
import com.ligero.beans.Beans;

/** The orders slice. Its beans depend on the catalogue and customers slices —
 *  all modules share one Beans container, so cross-module wiring just resolves. */
public final class OrdersModule implements LigeroModule {

    @Override
    public void beans(Beans.Builder b) {
        b.bind(OrderRepository.class,      x -> new InMemoryOrderRepository());
        b.bind(NotificationService.class,  x -> new LoggingNotificationService());
        b.bind(OrderService.class,         x -> new DefaultOrderService(
            x.get(OrderRepository.class), x.get(CustomerService.class), x.get(CatalogService.class),
            x.get(PricingService.class), x.get(InventoryService.class), x.get(NotificationService.class)));
        b.bind(RecommendationService.class, x -> new DefaultRecommendationService(
            x.get(CatalogService.class), x.get(OrderRepository.class)));
        b.bind(OrderController.class,          x -> new OrderController(x.get(OrderService.class)));
        b.bind(RecommendationController.class, x -> new RecommendationController(x.get(RecommendationService.class)));
    }

    @Override
    public void routes(Ligero app, Beans beans) {
        beans.get(OrderController.class).register(app);
        beans.get(RecommendationController.class).register(app);
    }
}
