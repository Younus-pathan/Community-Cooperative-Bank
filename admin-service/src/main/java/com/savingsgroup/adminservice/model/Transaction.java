package com.savingsgroup.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String userId;
    private String groupId;
    private String type; // CONTRIBUTION, PAYOUT
    private BigDecimal amount;
    private String status; // PENDING, COMPLETED, FAILED
    private LocalDateTime date;
    private String description;
}