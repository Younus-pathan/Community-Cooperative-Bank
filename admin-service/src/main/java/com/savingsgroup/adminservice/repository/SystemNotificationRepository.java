package com.savingsgroup.adminservice.repository;

import com.savingsgroup.adminservice.model.SystemNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemNotificationRepository extends MongoRepository<SystemNotification, String> {
    Page<SystemNotification> findByTargetUserIdsContaining(String userId, Pageable pageable);
    Page<SystemNotification> findByTargetGroupIdsContaining(String groupId, Pageable pageable);
    Page<SystemNotification> findByExpiresAtAfter(LocalDateTime now, Pageable pageable);
    List<SystemNotification> findByExpiresAtBeforeAndIsReadFalse(LocalDateTime now);
}
