package com.savingsgroup.authservice.dto;

import com.savingsgroup.authservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
