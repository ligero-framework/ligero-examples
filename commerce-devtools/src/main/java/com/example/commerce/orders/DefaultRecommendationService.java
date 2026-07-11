package com.example.commerce.orders;

import com.example.commerce.catalog.CatalogService;
import com.example.commerce.model.CatalogItem;
import com.example.commerce.model.Order;
import com.example.commerce.model.OrderLine;
import com.ligero.beans.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultRecommendationService implements RecommendationService {

    private final CatalogService catalog;
    private final OrderRepository orders;

    public DefaultRecommendationService(CatalogService catalog, OrderRepository orders) {
        this.catalog = catalog;
        this.orders = orders;
    }

    @Override
    public List<CatalogItem> forCustomer(long customerId) {
        Set<String> bought = orders.byCustomer(customerId).stream()
            .flatMap(o -> o.lines().stream()).map(OrderLine::sku).collect(Collectors.toSet());
        // recommend other in-stock products the customer hasn't bought yet
        return catalog.catalog().stream()
            .filter(CatalogItem::inStock)
            .filter(item -> !bought.contains(item.product().sku()))
            .limit(3)
            .toList();
    }
}
