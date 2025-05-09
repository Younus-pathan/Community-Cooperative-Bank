package com.savingsgroup.authservice.service;

import com.savingsgroup.authservice.client.NotificationRequest;
import com.savingsgroup.authservice.client.NotificationServiceClient;
import com.savingsgroup.authservice.client.UserServiceClient;
import com.savingsgroup.authservice.dto.*;
import com.savingsgroup.authservice.exception.CustomException;
import com.savingsgroup.authservice.exception.ResourceNotFoundException;
import com.savingsgroup.authservice.model.PasswordResetToken;
import com.savingsgroup.authservice.model.Role;
import com.savingsgroup.authservice.model.User;
import com.savingsgroup.authservice.repository.PasswordResetTokenRepository;
import com.savingsgroup.authservice.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "registerFallback")
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("Username is already taken", HttpStatus.BAD_REQUEST);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email is already registered", HttpStatus.BAD_REQUEST);
        }

        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Create user in user-service using Feign client
        UserResponse userResponse = UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .phoneNumber(savedUser.getPhoneNumber())
                .build();

        try {
            userServiceClient.createUser(userResponse, "Bearer " + accessToken);

            // Send welcome notification
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .userId(savedUser.getId())
                    .title("Welcome to Savings Group")
                    .message("Thank you for registering with us. Your account has been created successfully.")
                    .type("WELCOME")
                    .build();

            notificationServiceClient.sendNotification(notificationRequest, "Bearer " + accessToken);
        } catch (Exception e) {
            // Handle exceptions, but don't fail registration process
            System.out.println("Error communicating with other services: " + e.getMessage());
        }

        return AuthResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    // Fallback method for register
    public AuthResponse registerFallback(RegisterRequest request, Throwable throwable) {
        // We still register the user in our database but inform that some features might be limited
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Log the error but continue with limited functionality
        System.out.println("User service is unavailable. Created user in auth service but not in user service. " +
                "Reason: " + throwable.getMessage());

        AuthResponse response = AuthResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return response;
    }

    public AuthResponse authenticate(AuthRequest request) {
        // Determine if input is username or email
        String username = request.getUsernameOrEmail();
        User user = null;

        // Try to find user by username or email
        if (username.contains("@")) {
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
        } else {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        }

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new CustomException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken) // Re-use the same refresh token if it's still valid
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    public UserResponse validateToken(String token) {
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (!jwtService.isTokenValid(token, user)) {
            throw new CustomException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request, String baseUrl) {
        String email = request.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Check if a token already exists for this user
        Optional<PasswordResetToken> existingToken = tokenRepository.findByUserId(user.getId());

        // If a token exists, delete it
        existingToken.ifPresent(tokenRepository::delete);

        // Generate a new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusHours(1)) // Token expires in 1 hour
                .build();

        tokenRepository.save(passwordResetToken);

        // Send email with reset link
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        String emailBody = "Hello " + user.getFirstName() + ",\n\n" +
                "You have requested to reset your password. Please use the link below to reset your password:\n\n" +
                resetUrl + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Regards,\nSavings Group Team";

        emailService.sendEmail(user.getEmail(), "Password Reset Request", emailBody);

        // Try to send notification if the notification service is available
        try {
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .userId(user.getId())
                    .title("Password Reset Requested")
                    .message("A password reset was requested for your account. If this wasn't you, please secure your account.")
                    .type("SECURITY_ALERT")
                    .build();

            // Since we don't have a valid token here, we'll use a system token or skip this
            // In a real application, you would have a service account token
            notificationServiceClient.sendNotification(notificationRequest, null);
        } catch (Exception e) {
            // Log the error but continue
            System.out.println("Failed to send notification: " + e.getMessage());
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        // Find the token
        PasswordResetToken passwordResetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new CustomException("Invalid or expired token", HttpStatus.BAD_REQUEST));

        // Check if token is expired
        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(passwordResetToken);
            throw new CustomException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        // Find the user
        User user = userRepository.findById(passwordResetToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Delete the token
        tokenRepository.delete(passwordResetToken);

        // Try to send notification if the notification service is available
        try {
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .userId(user.getId())
                    .title("Password Changed Successfully")
                    .message("Your password has been changed successfully. If you didn't make this change, please contact us immediately.")
                    .type("SECURITY_UPDATE")
                    .build();

            // Generate a system token for internal communication
            Map<String, Object> claims = new HashMap<>();
            claims.put("scope", "internal");
            String systemToken = jwtService.generateTokenWithClaims(user, claims);

            notificationServiceClient.sendNotification(notificationRequest, "Bearer " + systemToken);
        } catch (Exception e) {
            // Log the error but continue
            System.out.println("Failed to send notification: " + e.getMessage());
        }
    }

    public void logout(String token) {
        // In a more robust implementation, you would add the token to a blacklist
        // or use Redis to track invalidated tokens until their expiration

        // For simplicity, we'll just log the logout
        String username = jwtService.extractUsername(token);
        System.out.println("User " + username + " has logged out");

        // In a real implementation, you would do:
        // tokenBlacklistService.blacklistToken(token, jwtService.getExpirationFromToken(token));
    }

    // Add this method to support generating tokens with custom claims
    public Map<String, Object> extractAllClaims(String token) {
        return jwtService.extractAllClaims(token);
    }
}


