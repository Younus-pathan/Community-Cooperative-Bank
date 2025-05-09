package com.savingsgroup.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "groups")
public class Group {
    @Id
    private String id;
    private String name;
    private String description;
    private String organizerId;
    private List<String> memberIds;
    private int totalMembers;
    private BigDecimal contributionAmount;
    private String frequency;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPublic;
    private boolean isBlocked;
    private String blockReason;
    private List<Map<String, Object>> joinRequests;
}