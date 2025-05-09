package com.savingsgroup.adminservice.service;


import com.savingsgroup.adminservice.client.UserServiceClient;
import com.savingsgroup.adminservice.dto.AdminActionDTO;
import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.UserDTO;
import com.savingsgroup.adminservice.model.AdminAction;
import com.savingsgroup.adminservice.repository.AdminActionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserServiceClient userServiceClient;
    private final AdminActionRepository adminActionRepository;

    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsersFallback")
    public List<UserDTO> getAllUsers() {
        ApiResponse<List<UserDTO>> response = userServiceClient.getAllUsers();
        if (response.isSuccess()) {
            return response.getData();
        }
        log.error("Failed to retrieve users: {}", response.getMessage());
        return Collections.emptyList();
    }

    public List<UserDTO> getAllUsersFallback(Exception e) {
        log.error("Fallback: Failed to retrieve users due to: {}", e.getMessage());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public UserDTO getUserById(String userId) {
        ApiResponse<UserDTO> response = userServiceClient.getUserById(userId);
        if (response.isSuccess()) {
            return response.getData();
        }
        log.error("Failed to retrieve user {}: {}", userId, response.getMessage());
        return null;
    }

    public UserDTO getUserByIdFallback(String userId, Exception e) {
        log.error("Fallback: Failed to retrieve user {} due to: {}", userId, e.getMessage());
        return null;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "blockUserFallback")
    public boolean blockUser(String userId, AdminActionDTO adminActionDTO) {
        try {
            ApiResponse<Void> response = userServiceClient.blockUser(userId, adminActionDTO.getReason());
            if (response.isSuccess()) {
                saveAdminAction(userId, "USER_BLOCK", adminActionDTO.getReason());
                return true;
            }
            log.error("Failed to block user {}: {}", userId, response.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error blocking user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public boolean blockUserFallback(String userId, AdminActionDTO adminActionDTO, Exception e) {
        log.error("Fallback: Failed to block user {} due to: {}", userId, e.getMessage());
        return false;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "unblockUserFallback")
    public boolean unblockUser(String userId) {
        try {
            ApiResponse<Void> response = userServiceClient.unblockUser(userId);
            if (response.isSuccess()) {
                saveAdminAction(userId, "USER_UNBLOCK", "Admin unblocked user");
                return true;
            }
            log.error("Failed to unblock user {}: {}", userId, response.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error unblocking user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public boolean unblockUserFallback(String userId, Exception e) {
        log.error("Fallback: Failed to unblock user {} due to: {}", userId, e.getMessage());
        return false;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "deleteUserFallback")
    public boolean deleteUser(String userId, AdminActionDTO adminActionDTO) {
        try {
            ApiResponse<Void> response = userServiceClient.deleteUser(userId);
            if (response.isSuccess()) {
                saveAdminAction(userId, "USER_DELETE", adminActionDTO.getReason());
                return true;
            }
            log.error("Failed to delete user {}: {}", userId, response.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public boolean deleteUserFallback(String userId, AdminActionDTO adminActionDTO, Exception e) {
        log.error("Fallback: Failed to delete user {} due to: {}", userId, e.getMessage());
        return false;
    }

    public Page<AdminAction> getAdminActionsByUser(String userId, Pageable pageable) {
        return adminActionRepository.findByTargetId(userId, pageable);
    }

    private void saveAdminAction(String targetId, String actionType, String reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        AdminAction adminAction = AdminAction.builder()
                .adminId(adminId)
                .actionType(actionType)
                .targetId(targetId)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();

        adminActionRepository.save(adminAction);
    }
}