package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.GoodsReceiptNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for GRN (Goods Receipt Note) operations
 */
public interface GoodsReceiptNoteRepository extends MongoRepository<GoodsReceiptNote, String> {
    
    /**
     * Find all GRNs for a specific Purchase Order
     */
    List<GoodsReceiptNote> findByPurchaseOrderId(String purchaseOrderId);
    
    /**
     * Find all GRNs for a specific Supplier
     */
    List<GoodsReceiptNote> findBySupplierId(String supplierId);
    
    /**
     * Find all GRNs by status
     */
    List<GoodsReceiptNote> findByStatus(String status);
}
