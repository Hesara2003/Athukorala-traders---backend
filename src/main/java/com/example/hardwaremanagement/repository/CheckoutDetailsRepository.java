package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.CheckoutDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CheckoutDetailsRepository extends MongoRepository<CheckoutDetails, String> {
    Optional<CheckoutDetails> findFirstByCustomerIdOrderByUpdatedAtDesc(String customerId);
}
