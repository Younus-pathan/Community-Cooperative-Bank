package com.savingsgroup.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "notification-service", fallback = NotificationServiceFallback.class)
public interface NotificationServiceClient {

    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request, @RequestHeader("Authorization") String authToken);
}
