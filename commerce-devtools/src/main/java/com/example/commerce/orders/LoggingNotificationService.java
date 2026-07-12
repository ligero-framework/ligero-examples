package com.example.commerce.orders;

import com.ligero.beans.stereotype.Component;

@Component
public class LoggingNotificationService implements NotificationService {

    @Override
    public void orderConfirmed(String orderId, String email) {
        System.out.println("[notify] order " + orderId + " confirmed -> " + email);
    }
}
