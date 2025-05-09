package com.savingsgroup.adminservice.repository;


import com.savingsgroup.adminservice.model.SystemMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemMetricsRepository extends MongoRepository<SystemMetrics, String> {
    SystemMetrics findTopByOrderByLastUpdatedDesc();
}
