package com.savingsgroup.adminservice.repository;


import com.savingsgroup.adminservice.model.AdminAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AdminActionRepository extends MongoRepository<AdminAction, String> {
    Page<AdminAction> findByAdminId(String adminId, Pageable pageable);
    Page<AdminAction> findByTargetId(String targetId, Pageable pageable);
    Page<AdminAction> findByActionType(String actionType, Pageable pageable);
    Page<AdminAction> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}