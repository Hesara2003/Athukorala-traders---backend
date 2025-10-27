package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Supplier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SupplierRepository extends MongoRepository<Supplier, String> {
    Optional<Supplier> findByName(String name);
}
