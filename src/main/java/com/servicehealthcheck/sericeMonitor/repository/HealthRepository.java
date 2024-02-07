package com.servicehealthcheck.sericeMonitor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.servicehealthcheck.sericeMonitor.model.HealthStatus;

public interface HealthRepository extends MongoRepository<HealthStatus, String> {
}