package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {
}
