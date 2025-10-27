package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesRepository extends MongoRepository<Sale, String> {
    List<Sale> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
