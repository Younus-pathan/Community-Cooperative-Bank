package com.savingsgroup.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private boolean isActive;
    private List<String> groupIds;
    private LocalDateTime createdAt;
    private boolean isBlocked;
    private String blockReason;
}
