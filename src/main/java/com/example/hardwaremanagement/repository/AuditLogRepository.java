package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
