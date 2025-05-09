package com.savingsgroup.adminservice.controller;

import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.model.AdminAction;
import com.savingsgroup.adminservice.repository.AdminActionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/actions")
@RequiredArgsConstructor
@Slf4j
public class AdminActionController {

    private final AdminActionRepository adminActionRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminAction>>> getAllAdminActions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching all admin actions");
        Page<AdminAction> actions = adminActionRepository.findAll(
                PageRequest.of(page, size, Sort.by("timestamp").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Admin actions retrieved successfully", actions));
    }

    @GetMapping("/by-admin/{adminId}")
    public ResponseEntity<ApiResponse<Page<AdminAction>>> getActionsByAdmin(
            @PathVariable String adminId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching admin actions performed by admin with ID: {}", adminId);
        Page<AdminAction> actions = adminActionRepository.findByAdminId(
                adminId,
                PageRequest.of(page, size, Sort.by("timestamp").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Admin actions retrieved successfully", actions));
    }

    @GetMapping("/by-type/{actionType}")
    public ResponseEntity<ApiResponse<Page<AdminAction>>> getActionsByType(
            @PathVariable String actionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching admin actions of type: {}", actionType);
        Page<AdminAction> actions = adminActionRepository.findByActionType(
                actionType,
                PageRequest.of(page, size, Sort.by("timestamp").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Admin actions retrieved successfully", actions));
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<ApiResponse<Page<AdminAction>>> getActionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching admin actions between {} and {}", startDate, endDate);
        Page<AdminAction> actions = adminActionRepository.findByTimestampBetween(
                startDate,
                endDate,
                PageRequest.of(page, size, Sort.by("timestamp").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Admin actions retrieved successfully", actions));
    }
}