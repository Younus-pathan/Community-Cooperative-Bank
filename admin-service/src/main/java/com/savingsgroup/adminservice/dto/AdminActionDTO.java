package com.savingsgroup.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionDTO {
    @NotBlank(message = "Target ID is required")
    private String targetId;

    @NotBlank(message = "Reason is required")
    private String reason;
}