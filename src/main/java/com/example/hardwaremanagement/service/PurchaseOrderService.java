package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.PurchaseOrder;
import com.example.hardwaremanagement.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public List<PurchaseOrder> list() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrder> get(String id) {
        return purchaseOrderRepository.findById(id);
    }

    public PurchaseOrder create(PurchaseOrder po) {
        return purchaseOrderRepository.save(po);
    }

    public PurchaseOrder update(String id, PurchaseOrder update) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));
        if (!"CREATED".equalsIgnoreCase(existing.getStatus())) {
            throw new IllegalStateException("Only POs in CREATED status can be edited");
        }
        // Update editable fields
        existing.setSupplierId(update.getSupplierId());
        existing.setItems(update.getItems());
        existing.setProductIds(update.getProductIds());
        existing.setDeliveryDate(update.getDeliveryDate());
        // Keep status and createdAt as-is
        return purchaseOrderRepository.save(existing);
    }

    public PurchaseOrder updateDeliveryDate(String id, java.time.LocalDate newDeliveryDate) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));
        
        // Allow delivery date updates for CREATED, APPROVED status
        if (!"CREATED".equalsIgnoreCase(existing.getStatus()) && 
            !"APPROVED".equalsIgnoreCase(existing.getStatus())) {
            throw new IllegalStateException("Delivery date can only be updated for POs in CREATED or APPROVED status");
        }
        
        existing.setDeliveryDate(newDeliveryDate);
        return purchaseOrderRepository.save(existing);
    }

    public void delete(String id) {
        purchaseOrderRepository.deleteById(id);
    }

    public PurchaseOrder cancel(String id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));
        if (!"CREATED".equalsIgnoreCase(po.getStatus())) {
            throw new IllegalStateException("Only POs in CREATED status can be canceled");
        }
        po.setStatus("CANCELED");
        return purchaseOrderRepository.save(po);
    }

    public PurchaseOrder updateStatus(String id, String newStatus) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));
        
        String currentStatus = po.getStatus();
        
        // Validate status transitions
        boolean isValidTransition = false;
        if ("CREATED".equalsIgnoreCase(currentStatus) && 
            ("APPROVED".equalsIgnoreCase(newStatus) || "CANCELED".equalsIgnoreCase(newStatus))) {
            isValidTransition = true;
        } else if ("APPROVED".equalsIgnoreCase(currentStatus) && 
            ("SHIPPED".equalsIgnoreCase(newStatus) || "CANCELED".equalsIgnoreCase(newStatus))) {
            isValidTransition = true;
        } else if ("SHIPPED".equalsIgnoreCase(currentStatus) && 
            "DELIVERED".equalsIgnoreCase(newStatus)) {
            isValidTransition = true;
        } else if ("DELIVERED".equalsIgnoreCase(currentStatus) && 
            "RECEIVED".equalsIgnoreCase(newStatus)) {
            isValidTransition = true;
        }
        
        if (!isValidTransition) {
            throw new IllegalStateException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
        
        po.setStatus(newStatus.toUpperCase());
        return purchaseOrderRepository.save(po);
    }
}
