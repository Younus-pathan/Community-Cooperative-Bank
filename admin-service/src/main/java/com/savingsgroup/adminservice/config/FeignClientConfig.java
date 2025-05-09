package com.savingsgroup.adminservice.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Forward JWT token to the target service
                // This assumes that the JWT token is available in the Authorization header
                // and that it's properly propagated from the request to this service
                requestTemplate.header("Authorization", "Bearer " + authentication.getCredentials());
            }
        };
    }
}