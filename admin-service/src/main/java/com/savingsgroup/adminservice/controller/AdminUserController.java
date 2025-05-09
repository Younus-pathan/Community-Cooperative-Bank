package com.savingsgroup.adminservice.controller;

import com.savingsgroup.adminservice.dto.AdminActionDTO;
import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.UserDTO;
import com.savingsgroup.adminservice.model.AdminAction;
import com.savingsgroup.adminservice.service.AdminUserService;
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
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.info("Fetching all users");
        List<UserDTO> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String userId) {
        log.info("Fetching user with ID: {}", userId);
        UserDTO user = adminUserService.getUserById(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found with ID: " + userId));
        }

        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    @PutMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<Void>> blockUser(
            @PathVariable String userId,
            @RequestBody @Valid AdminActionDTO adminActionDTO) {

        log.info("Blocking user with ID: {}", userId);
        boolean success = adminUserService.blockUser(userId, adminActionDTO);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to block user with ID: " + userId));
        }

        return ResponseEntity.ok(ApiResponse.success("User blocked successfully", null));
    }

    @PutMapping("/{userId}/unblock")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@PathVariable String userId) {
        log.info("Unblocking user with ID: {}", userId);
        boolean success = adminUserService.unblockUser(userId);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to unblock user with ID: " + userId));
        }

        return ResponseEntity.ok(ApiResponse.success("User unblocked successfully", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String userId,
            @RequestBody @Valid AdminActionDTO adminActionDTO) {

        log.info("Deleting user with ID: {}", userId);
        boolean success = adminUserService.deleteUser(userId, adminActionDTO);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete user with ID: " + userId));
        }

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @GetMapping("/{userId}/actions")
    public ResponseEntity<ApiResponse<Page<AdminAction>>> getUserActions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching admin actions for user with ID: {}", userId);
        Page<AdminAction> actions = adminUserService.getAdminActionsByUser(
                userId,
                PageRequest.of(page, size, Sort.by("timestamp").descending())
        );

        return ResponseEntity.ok(ApiResponse.success("Admin actions retrieved successfully", actions));
    }
}