package com.savingsgroup.adminservice.service;


import com.savingsgroup.adminservice.client.GamificationServiceClient;
import com.savingsgroup.adminservice.client.TransactionServiceClient;
import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.SystemMetricsDTO;
import com.savingsgroup.adminservice.model.SystemMetrics;
import com.savingsgroup.adminservice.repository.SystemMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemMetricsService {

    private final SystemMetricsRepository systemMetricsRepository;
    private final AdminUserService adminUserService;
    private final AdminGroupService adminGroupService;
    private final TransactionServiceClient transactionServiceClient;
    private final GamificationServiceClient gamificationServiceClient;

    public SystemMetricsDTO getLatestSystemMetrics() {
        SystemMetrics metrics = systemMetricsRepository.findTopByOrderByLastUpdatedDesc();
        if (metrics == null) {
            // Return empty metrics if none exist yet
            return new SystemMetricsDTO();
        }

        return mapToDTO(metrics);
    }

    @Scheduled(cron = "0 0 * * * *") // Run once per hour
    public void collectAndStoreSystemMetrics() {
        try {
            log.info("Starting system metrics collection");

            int totalUsers = adminUserService.getAllUsers().size();
            int totalGroups = adminGroupService.getAllGroups().size();

            // Count active groups
            int activeGroups = (int) adminGroupService.getAllGroups().stream()
                    .filter(group -> "ACTIVE".equals(group.getStatus()))
                    .count();

            // Get transaction volume
            BigDecimal totalTransactionVolume = BigDecimal.ZERO;
            try {
                ApiResponse<BigDecimal> volumeResponse = transactionServiceClient.getTotalTransactionVolume();
                if (volumeResponse.isSuccess() && volumeResponse.getData() != null) {
                    totalTransactionVolume = volumeResponse.getData();
                }
            } catch (Exception e) {
                log.error("Error fetching transaction volume: {}", e.getMessage());
            }

            // Get region data (simplified example)
            Map<String, Integer> usersByRegion = new HashMap<>();
            Map<String, Integer> groupsByRegion = new HashMap<>();

            // Save the metrics
            SystemMetrics metrics = SystemMetrics.builder()
                    .totalUsers(totalUsers)
                    .totalGroups(totalGroups)
                    .activeGroups(activeGroups)
                    .totalTransactionVolume(totalTransactionVolume)
                    .usersByRegion(usersByRegion)
                    .groupsByRegion(groupsByRegion)
                    .lastUpdated(LocalDateTime.now())
                    .build();

            systemMetricsRepository.save(metrics);
            log.info("System metrics collection completed");

        } catch (Exception e) {
            log.error("Error collecting system metrics: {}", e.getMessage());
        }
    }

    private SystemMetricsDTO mapToDTO(SystemMetrics metrics) {
        return SystemMetricsDTO.builder()
                .totalUsers(metrics.getTotalUsers())
                .totalGroups(metrics.getTotalGroups())
                .activeGroups(metrics.getActiveGroups())
                .totalTransactionVolume(metrics.getTotalTransactionVolume())
                .usersByRegion(metrics.getUsersByRegion())
                .groupsByRegion(metrics.getGroupsByRegion())
                .lastUpdated(metrics.getLastUpdated())
                .build();
    }
}