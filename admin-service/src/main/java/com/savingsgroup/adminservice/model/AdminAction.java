package com.savingsgroup.adminservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "admin_actions")
public class AdminAction {
    @Id
    private String id;
    private String adminId;
    private String actionType; // USER_BLOCK, USER_UNBLOCK, GROUP_BLOCK, GROUP_UNBLOCK, USER_DELETE, GROUP_DELETE
    private String targetId; // User ID or Group ID that was acted upon
    private String reason;
    private LocalDateTime timestamp;
}