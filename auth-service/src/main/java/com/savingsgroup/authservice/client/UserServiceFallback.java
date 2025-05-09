package com.savingsgroup.authservice.client;


import com.savingsgroup.authservice.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserServiceClient {

    @Override
    public UserResponse createUser(UserResponse userResponse, String authToken) {
        // Return a default response or throw an exception
        throw new RuntimeException("User service is currently unavailable. Please try again later.");
    }

    @Override
    public UserResponse getUserById(String userId, String authToken) {
        // Return a default response or throw an exception
        throw new RuntimeException("User service is currently unavailable. Please try again later.");
    }

    @Override
    public UserResponse updateUser(String userId, UserResponse userResponse, String authToken) {
        // Return a default response or throw an exception
        throw new RuntimeException("User service is currently unavailable. Please try again later.");
    }
}
