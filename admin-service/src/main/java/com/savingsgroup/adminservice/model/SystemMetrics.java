package com.savingsgroup.adminservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "system_metrics")
public class SystemMetrics {
    @Id
    private String id;
    private int totalUsers;
    private int totalGroups;
    private int activeGroups;
    private BigDecimal totalTransactionVolume;
    private Map<String, Integer> usersByRegion;
    private Map<String, Integer> groupsByRegion;
    private LocalDateTime lastUpdated;
}