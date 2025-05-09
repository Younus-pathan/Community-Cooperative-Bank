package com.savingsgroup.adminservice.client;

import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users")
    ApiResponse<List<UserDTO>> getAllUsers();

    @GetMapping("/api/users/{userId}")
    ApiResponse<UserDTO> getUserById(@PathVariable String userId);

    @PutMapping("/api/users/{userId}/block")
    ApiResponse<Void> blockUser(@PathVariable String userId, @RequestBody String reason);

    @PutMapping("/api/users/{userId}/unblock")
    ApiResponse<Void> unblockUser(@PathVariable String userId);

    @DeleteMapping("/api/users/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable String userId);
}

