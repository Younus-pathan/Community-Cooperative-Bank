package com.savingsgroup.adminservice.service;

import com.savingsgroup.adminservice.client.GroupServiceClient;
import com.savingsgroup.adminservice.dto.AdminActionDTO;
import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.GroupDTO;
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
public class AdminGroupService {

    private final GroupServiceClient groupServiceClient;
    private final AdminActionRepository adminActionRepository;

    @CircuitBreaker(name = "groupService", fallbackMethod = "getAllGroupsFallback")
    public List<GroupDTO> getAllGroups() {
        ApiResponse<List<GroupDTO>> response = groupServiceClient.getAllGroups();
        if (response.isSuccess()) {
            return response.getData();
        }
        log.error("Failed to retrieve groups: {}", response.getMessage());
        return Collections.emptyList();
    }

    public List<GroupDTO> getAllGroupsFallback(Exception e) {
        log.error("Fallback: Failed to retrieve groups due to: {}", e.getMessage());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "groupService", fallbackMethod = "getGroupByIdFallback")
    public GroupDTO getGroupById(String groupId) {
        ApiResponse<GroupDTO> response = groupServiceClient.getGroupById(groupId);
        if (response.isSuccess()) {
            return response.getData();
        }
        log.error("Failed to retrieve group {}: {}", groupId, response.getMessage());
        return null;
    }

    public GroupDTO getGroupByIdFallback(String groupId, Exception e) {
        log.error("Fallback: Failed to retrieve group {} due to: {}", groupId, e.getMessage());
        return null;
    }

    @CircuitBreaker(name = "groupService", fallbackMethod = "blockGroupFallback")
    public boolean blockGroup(String groupId, AdminActionDTO adminActionDTO) {
        try {
            ApiResponse<Void> response = groupServiceClient.blockGroup(groupId, adminActionDTO.getReason());
            if (response.isSuccess()) {
                saveAdminAction(groupId, "GROUP_BLOCK", adminActionDTO.getReason());
                return true;
            }
            log.error("Failed to block group {}: {}", groupId, response.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error blocking group {}: {}", groupId, e.getMessage());
            return false;
        }
    }

    public boolean blockGroupFallback(String groupId, AdminActionDTO adminActionDTO, Exception e) {
        log.error("Fallback: Failed to block group {} due to: {}", groupId, e.getMessage());
        return false;
    }

    @CircuitBreaker(name = "groupService", fallbackMethod = "unblockGroupFallback")
    public boolean unblockGroup(String groupId) {
        try {
            ApiResponse<Void> response = groupServiceClient.unblockGroup(groupId);
            if (response.isSuccess()) {
                saveAdminAction(groupId, "GROUP_UNBLOCK", "Admin unblocked group");
                return true;
            }
            log.error("Failed to unblock group {}: {}", groupId, response.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error unblocking group {}: {}", groupId, e.getMessage());
            return false;
        }
    }

    public boolean unblockGroupFallback(String groupId, Exception e) {
        log.error("Fallback: Failed to unblock group {} due to: {}", groupId, e.getMessage());
        return false;
    }

    @CircuitBreaker(name = "groupService", fallbackMethod = "deleteGroupFallback")
    public boolean deleteGroup(String groupId, AdminActionDTO adminActionDTO) {
        try {
            ApiResponse<Void> response = groupServiceClient.deleteGroup(groupId);
            if (response.isSuccess()) {
                saveAdminAction(groupId, "GROUP_DELETE", adminActionDTO.getReason());
                return true;
            }
            log.error("Failed to delete group {}: {}", groupId, response.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error deleting group {}: {}", groupId, e.getMessage());
            return false;
        }
    }

    public boolean deleteGroupFallback(String groupId, AdminActionDTO adminActionDTO, Exception e) {
        log.error("Fallback: Failed to delete group {} due to: {}", groupId, e.getMessage());
        return false;
    }

    public Page<AdminAction> getAdminActionsByGroup(String groupId, Pageable pageable) {
        return adminActionRepository.findByTargetId(groupId, pageable);
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