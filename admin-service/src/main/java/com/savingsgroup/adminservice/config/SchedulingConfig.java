package com.savingsgroup.adminservice.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Configuration for scheduling tasks
    // The actual scheduling is defined in the SystemMetricsService class
}