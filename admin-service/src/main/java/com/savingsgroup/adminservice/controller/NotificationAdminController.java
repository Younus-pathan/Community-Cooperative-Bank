package com.savingsgroup.adminservice.controller;


import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.SystemNotificationDTO;
import com.savingsgroup.adminservice.model.SystemNotification;
import com.savingsgroup.adminservice.service.NotificationAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationAdminController {

    private final NotificationAdminService notificationAdminService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendNotification(@RequestBody @Valid SystemNotificationDTO notificationDTO) {
        log.info("Sending system notification: {}", notificationDTO.getTitle());
        boolean success = notificationAdminService.sendSystemNotification(notificationDTO);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to send notification"));
        }

        return ResponseEntity.ok(ApiResponse.success("Notification sent successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SystemNotification>>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching all notifications");
        Page<SystemNotification> notifications = notificationAdminService.getAllNotifications(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<SystemNotification>>> getActiveNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching active notifications");
        Page<SystemNotification> notifications = notificationAdminService.getActiveNotifications(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Active notifications retrieved successfully", notifications));
    }
}