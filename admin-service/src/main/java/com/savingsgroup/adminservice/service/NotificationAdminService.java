package com.savingsgroup.adminservice.service;


import com.savingsgroup.adminservice.client.NotificationServiceClient;
import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.SystemNotificationDTO;
import com.savingsgroup.adminservice.model.SystemNotification;
import com.savingsgroup.adminservice.repository.SystemNotificationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationAdminService {

    private final NotificationServiceClient notificationServiceClient;
    private final SystemNotificationRepository systemNotificationRepository;

    @CircuitBreaker(name = "notificationService", fallbackMethod = "sendSystemNotificationFallback")
    public boolean sendSystemNotification(SystemNotificationDTO notificationDTO) {
        try {
            ApiResponse<Void> response;

            // Check if it's a targeted or broadcast notification
            if ((notificationDTO.getTargetUserIds() != null && !notificationDTO.getTargetUserIds().isEmpty()) ||
                    (notificationDTO.getTargetGroupIds() != null && !notificationDTO.getTargetGroupIds().isEmpty())) {
                response = notificationServiceClient.sendNotification(notificationDTO);
            } else {
                response = notificationServiceClient.broadcastNotification(notificationDTO);
            }

            if (response.isSuccess()) {
                saveNotification(notificationDTO);
                return true;
            }

            log.error("Failed to send notification: {}", response.getMessage());
            return false;

        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            return false;
        }
    }

    public boolean sendSystemNotificationFallback(SystemNotificationDTO notificationDTO, Exception e) {
        log.error("Fallback: Failed to send notification due to: {}", e.getMessage());

        // Still save the notification for record keeping, but mark as potentially undelivered
        SystemNotification notification = SystemNotification.builder()
                .title(notificationDTO.getTitle())
                .message(notificationDTO.getMessage() + " [DELIVERY MAY HAVE FAILED]")
                .type(notificationDTO.getType())
                .targetUserIds(notificationDTO.getTargetUserIds())
                .targetGroupIds(notificationDTO.getTargetGroupIds())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(notificationDTO.getExpiresAt())
                .build();

        systemNotificationRepository.save(notification);

        return false;
    }

    public Page<SystemNotification> getAllNotifications(Pageable pageable) {
        return systemNotificationRepository.findAll(pageable);
    }

    public Page<SystemNotification> getActiveNotifications(Pageable pageable) {
        return systemNotificationRepository.findByExpiresAtAfter(LocalDateTime.now(), pageable);
    }

    private void saveNotification(SystemNotificationDTO dto) {
        SystemNotification notification = SystemNotification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .targetUserIds(dto.getTargetUserIds())
                .targetGroupIds(dto.getTargetGroupIds())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(dto.getExpiresAt())
                .build();

        systemNotificationRepository.save(notification);
    }
}
