package com.savingsgroup.authservice.client;

import com.savingsgroup.authservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", fallback = UserServiceFallback.class)
public interface UserServiceClient {

    @PostMapping("/api/users/create")
    UserResponse createUser(@RequestBody UserResponse userResponse, @RequestHeader("Authorization") String authToken);

    @GetMapping("/api/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId, @RequestHeader("Authorization") String authToken);

    @PutMapping("/api/users/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId, @RequestBody UserResponse userResponse, @RequestHeader("Authorization") String authToken);
}
