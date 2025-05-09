package com.savingsgroup.adminservice.client;


import com.savingsgroup.adminservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "transaction-service")
public interface TransactionServiceClient {
    @GetMapping("/api/transactions/reports/total-volume")
    ApiResponse<BigDecimal> getTotalTransactionVolume();

    @GetMapping("/api/transactions/reports/volume-by-group")
    ApiResponse<Map<String, BigDecimal>> getTransactionVolumeByGroup();

    @GetMapping("/api/transactions/reports/transactions-count")
    ApiResponse<Integer> getTotalTransactionsCount();
}
