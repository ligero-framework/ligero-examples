package com.example.commerce.orders;

/** Fire-and-forget notifications — a @Component (no dependencies). */
public interface NotificationService {
    void orderConfirmed(String orderId, String email);
}
