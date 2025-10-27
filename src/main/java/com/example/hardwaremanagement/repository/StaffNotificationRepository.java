package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.StaffNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffNotificationRepository extends MongoRepository<StaffNotification, String> {
    
    List<StaffNotification> findAllByOrderByCreatedAtDesc();
    
    List<StaffNotification> findByPriorityOrderByCreatedAtDesc(String priority);
    
    List<StaffNotification> findByReadOrderByCreatedAtDesc(boolean read);
    
    List<StaffNotification> findByPurchaseOrderIdOrderByCreatedAtDesc(String purchaseOrderId);
    
    List<StaffNotification> findBySupplierIdOrderByCreatedAtDesc(String supplierId);
    
    List<StaffNotification> findByTypeOrderByCreatedAtDesc(String type);
    
    long countByReadFalse();
    
    long countByPriorityAndReadFalse(String priority);
    
    long countByTypeAndReadFalse(String type);
}
