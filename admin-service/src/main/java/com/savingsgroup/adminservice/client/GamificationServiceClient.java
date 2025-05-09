package com.savingsgroup.adminservice.client;

import com.savingsgroup.adminservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "gamification-service")
public interface GamificationServiceClient {
    @GetMapping("/api/gamification/analytics/summary")
    ApiResponse<Map<String, Object>> getGamificationAnalyticsSummary();
}