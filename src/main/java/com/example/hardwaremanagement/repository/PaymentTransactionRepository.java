package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.PaymentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {
    List<PaymentTransaction> findByOrderIdOrderByCreatedAtDesc(String orderId);
    List<PaymentTransaction> findByCustomerReferenceOrderByCreatedAtDesc(String customerReference);
    List<PaymentTransaction> findByPaymentIntentIdOrderByCreatedAtDesc(String paymentIntentId);
}
