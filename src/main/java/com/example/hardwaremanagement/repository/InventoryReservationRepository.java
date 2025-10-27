package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.InventoryReservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryReservationRepository extends MongoRepository<InventoryReservation, String> {
    List<InventoryReservation> findByProductIdAndStatus(String productId, String status);
    List<InventoryReservation> findByOrderId(String orderId);
    Optional<InventoryReservation> findByOrderIdAndProductId(String orderId, String productId);
    List<InventoryReservation> findByStatusAndExpiresAtBefore(String status, LocalDateTime time);
    List<InventoryReservation> findByCustomerId(String customerId);
}
