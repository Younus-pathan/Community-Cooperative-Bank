package com.savingsgroup.adminservice.controller;


import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.SystemMetricsDTO;
import com.savingsgroup.adminservice.service.SystemMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/metrics")
@RequiredArgsConstructor
@Slf4j
public class SystemMetricsController {

    private final SystemMetricsService systemMetricsService;

    @GetMapping
    public ResponseEntity<ApiResponse<SystemMetricsDTO>> getSystemMetrics() {
        log.info("Fetching system metrics");
        SystemMetricsDTO metrics = systemMetricsService.getLatestSystemMetrics();
        return ResponseEntity.ok(ApiResponse.success("System metrics retrieved successfully", metrics));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshSystemMetrics() {
        log.info("Manually refreshing system metrics");
        systemMetricsService.collectAndStoreSystemMetrics();
        return ResponseEntity.ok(ApiResponse.success("System metrics refreshed successfully", null));
    }
}