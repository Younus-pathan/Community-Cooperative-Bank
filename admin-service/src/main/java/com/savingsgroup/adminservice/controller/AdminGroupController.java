package com.savingsgroup.adminservice.controller;

import com.savingsgroup.adminservice.dto.AdminActionDTO;
import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.GroupDTO;
import com.savingsgroup.adminservice.model.AdminAction;
import com.savingsgroup.adminservice.service.AdminGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/groups")
@RequiredArgsConstructor
@Slf4j
public class AdminGroupController {

    private final AdminGroupService adminGroupService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupDTO>>> getAllGroups() {
        log.info("Fetching all groups");
        List<GroupDTO> groups = adminGroupService.getAllGroups();
        return ResponseEntity.ok(ApiResponse.success("Groups retrieved successfully", groups));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupDTO>> getGroupById(@PathVariable String groupId) {
        log.info("Fetching group with ID: {}", groupId);
        GroupDTO group = adminGroupService.getGroupById(groupId);

        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Group not found with ID: " + groupId));
        }

        return ResponseEntity.ok(ApiResponse.success("Group retrieved successfully", group));
    }

    @PutMapping("/{groupId}/block")
    public ResponseEntity<ApiResponse<Void>> blockGroup(
            @PathVariable String groupId,
            @RequestBody @Valid AdminActionDTO adminActionDTO) {

        log.info("Blocking group with ID: {}", groupId);
        boolean success = adminGroupService.blockGroup(groupId, adminActionDTO);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to block group with ID: " + groupId));
        }

        return ResponseEntity.ok(ApiResponse.success("Group blocked successfully", null));
    }

    @PutMapping("/{groupId}/unblock")
    public ResponseEntity<ApiResponse<Void>> unblockGroup(@PathVariable String groupId) {
        log.info("Unblocking group with ID: {}", groupId);
        boolean success = adminGroupService.unblockGroup(groupId);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to unblock group with ID: " + groupId));
        }

        return ResponseEntity.ok(ApiResponse.success("Group unblocked successfully", null));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable String groupId,
            @RequestBody @Valid AdminActionDTO adminActionDTO) {

        log.info("Deleting group with ID: {}", groupId);
        boolean success = adminGroupService.deleteGroup(groupId, adminActionDTO);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete group with ID: " + groupId));
        }

        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }

    @GetMapping("/{groupId}/actions")
    public ResponseEntity<ApiResponse<Page<AdminAction>>> getGroupActions(
            @PathVariable String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching admin actions for group with ID: {}", groupId);
        Page<AdminAction> actions = adminGroupService.getAdminActionsByGroup(
                groupId,
                PageRequest.of(page, size, Sort.by("timestamp").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Admin actions retrieved successfully", actions));
    }
}
