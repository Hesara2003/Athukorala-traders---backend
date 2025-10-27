package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.PurchaseOrder;
import com.example.hardwaremanagement.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier/purchase-orders")
@CrossOrigin(origins = "*")
public class SupplierPurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public List<PurchaseOrder> list() {
        return purchaseOrderService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> get(@PathVariable String id) {
        return purchaseOrderService.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> create(@RequestBody PurchaseOrder po) {
        if (po.getItems() != null && !po.getItems().isEmpty()) {
            po.setProductIds(po.getItems().stream().map(i -> i.getProductId()).toList());
        }
        po.setStatus("CREATED");
        if (po.getCreatedAt() == null) po.setCreatedAt(java.time.LocalDateTime.now());
        PurchaseOrder saved = purchaseOrderService.create(po);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody PurchaseOrder po) {
        try {
            if (po.getItems() != null && !po.getItems().isEmpty()) {
                po.setProductIds(po.getItems().stream().map(i -> i.getProductId()).toList());
            }
            PurchaseOrder updated = purchaseOrderService.update(id, po);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        try {
            PurchaseOrder canceled = purchaseOrderService.cancel(id);
            return ResponseEntity.ok(canceled);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/delivery-date")
    public ResponseEntity<?> updateDeliveryDate(@PathVariable String id, @RequestBody DeliveryDateUpdateRequest request) {
        try {
            // Using the DTO makes the controller cleaner. The service layer receives a strongly-typed date object.
            PurchaseOrder updated = purchaseOrderService.updateDeliveryDate(id, request.getDeliveryDate());
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody StatusUpdateRequest request) {
        try {
            PurchaseOrder updated = purchaseOrderService.updateStatus(id, request.getStatus());
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Inner class for the delivery date update request DTO
    public static class DeliveryDateUpdateRequest {
        private java.time.LocalDate deliveryDate;
        private String reason; // This field is available for future use

        public java.time.LocalDate getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(java.time.LocalDate deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    // Inner class for the status update request DTO
    public static class StatusUpdateRequest {
        private String status;
        private String notes; // Optional notes for the status update

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
