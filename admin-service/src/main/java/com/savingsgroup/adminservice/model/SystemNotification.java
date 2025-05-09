package com.savingsgroup.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "system_notifications")
public class SystemNotification {
    @Id
    private String id;
    private String title;
    private String message;
    private String type; // SYSTEM_UPDATE, MAINTENANCE, ANNOUNCEMENT
    private List<String> targetUserIds; // Empty for broadcast to all users
    private List<String> targetGroupIds; // Empty for broadcast to all groups
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
