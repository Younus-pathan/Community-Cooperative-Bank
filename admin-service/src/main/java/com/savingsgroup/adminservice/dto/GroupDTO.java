package com.savingsgroup.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {
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
    private boolean isPublic;
    private boolean isBlocked;
    private String blockReason;
}