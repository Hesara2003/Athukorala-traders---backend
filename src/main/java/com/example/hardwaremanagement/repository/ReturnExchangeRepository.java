package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.ReturnExchange;
import com.example.hardwaremanagement.model.ReturnExchangeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReturnExchangeRepository extends MongoRepository<ReturnExchange, String> {
    
    // Find all return/exchanges for a specific customer
    List<ReturnExchange> findByCustomerIdOrderByRequestedAtDesc(String customerId);
    
    // Find all return/exchanges for a specific order
    List<ReturnExchange> findByOrderId(String orderId);
    
    // Find by status
    List<ReturnExchange> findByStatus(ReturnExchangeStatus status);
    
    // Find pending and approved returns for staff review
    List<ReturnExchange> findByStatusInOrderByRequestedAtDesc(List<ReturnExchangeStatus> statuses);
    
    // Find returns processed by specific staff
    List<ReturnExchange> findByProcessedBy(String processedBy);
    
    // Find returns within date range
    List<ReturnExchange> findByRequestedAtBetween(LocalDateTime start, LocalDateTime end);
}
