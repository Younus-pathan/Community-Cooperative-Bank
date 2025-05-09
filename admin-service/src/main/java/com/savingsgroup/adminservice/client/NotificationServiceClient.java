package com.savingsgroup.adminservice.client;


import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.SystemNotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
    @PostMapping("/api/notifications/send")
    ApiResponse<Void> sendNotification(@RequestBody SystemNotificationDTO notification);

    @PostMapping("/api/notifications/broadcast")
    ApiResponse<Void> broadcastNotification(@RequestBody SystemNotificationDTO notification);
}
