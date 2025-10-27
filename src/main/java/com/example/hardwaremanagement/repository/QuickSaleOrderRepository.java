package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.QuickSaleOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuickSaleOrderRepository extends MongoRepository<QuickSaleOrder, String> {
}