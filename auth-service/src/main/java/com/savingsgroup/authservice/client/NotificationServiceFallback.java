package com.savingsgroup.authservice.client;


import org.springframework.stereotype.Component;

@Component
public class NotificationServiceFallback implements NotificationServiceClient {

    @Override
    public void sendNotification(NotificationRequest request, String authToken) {
        // Log the failure but continue processing
        System.out.println("Notification service is currently unavailable. Notification not sent.");
    }
}