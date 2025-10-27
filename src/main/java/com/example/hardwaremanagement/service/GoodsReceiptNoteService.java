package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.GoodsReceiptNote;
import com.example.hardwaremanagement.model.GRNItem;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.model.PurchaseOrder;
import com.example.hardwaremanagement.repository.GoodsReceiptNoteRepository;
import com.example.hardwaremanagement.repository.ProductRepository;
import com.example.hardwaremanagement.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for GRN (Goods Receipt Note) operations
 * Handles business logic for receiving goods and updating inventory
 */
@Service
public class GoodsReceiptNoteService {

    @Autowired
    private GoodsReceiptNoteRepository grnRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create a new GRN and update inventory automatically
     * @param grn The GRN to create
     * @return The created GRN with generated ID
     * @throws RuntimeException if Purchase Order is not found or already received
     */
    public GoodsReceiptNote createGRN(GoodsReceiptNote grn) {
        // Validate Purchase Order exists
        PurchaseOrder po = purchaseOrderRepository.findById(grn.getPurchaseOrderId())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + grn.getPurchaseOrderId()));

        // Check if PO is already received
        if ("RECEIVED".equalsIgnoreCase(po.getStatus())) {
            throw new IllegalStateException("Purchase Order has already been received");
        }

        // Set timestamps
        if (grn.getCreatedAt() == null) {
            grn.setCreatedAt(LocalDateTime.now());
        }
        grn.setUpdatedAt(LocalDateTime.now());

        if (grn.getReceivedDate() == null) {
            grn.setReceivedDate(LocalDateTime.now());
        }

        // Set supplier ID from PO if not provided
        if (grn.getSupplierId() == null || grn.getSupplierId().isEmpty()) {
            grn.setSupplierId(po.getSupplierId());
        }

        // Update inventory for each received item
        if (grn.getItems() != null && !grn.getItems().isEmpty()) {
            for (GRNItem item : grn.getItems()) {
                updateInventory(item);
            }

            // Determine GRN status based on received quantities
            grn.setStatus(determineGRNStatus(grn.getItems()));
        }

        // Save GRN
        GoodsReceiptNote savedGRN = grnRepository.save(grn);

        // Update Purchase Order status to RECEIVED
        po.setStatus("RECEIVED");
        purchaseOrderRepository.save(po);

        return savedGRN;
    }

    /**
     * Update product inventory based on received quantity
     * @param item The GRN item containing product and quantity information
     */
    private void updateInventory(GRNItem item) {
        Optional<Product> productOpt = productRepository.findById(item.getProductId());
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            
            // Add received quantity to stock
            int currentStock = product.getStock();
            int newStock = currentStock + item.getReceivedQuantity();
            product.setStock(newStock);
            
            // Set product name in GRN item for reporting
            item.setProductName(product.getName());
            
            // Update availability
            product.setAvailable(newStock > 0);
            
            // Save updated product
            productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found: " + item.getProductId());
        }
    }

    /**
     * Determine GRN status based on received vs ordered quantities
     * @param items List of GRN items
     * @return Status string (COMPLETED, PARTIAL, or DISCREPANCY)
     */
    private String determineGRNStatus(List<GRNItem> items) {
        boolean hasPartial = false;
        boolean hasDiscrepancy = false;

        for (GRNItem item : items) {
            if (item.getReceivedQuantity() == 0) {
                hasPartial = true;
            } else if (item.getReceivedQuantity() < item.getOrderedQuantity()) {
                hasPartial = true;
            } else if (item.getReceivedQuantity() > item.getOrderedQuantity()) {
                hasDiscrepancy = true;
            }

            // Check for damaged or defective items
            if (item.getCondition() != null && 
                ("DAMAGED".equalsIgnoreCase(item.getCondition()) || 
                 "DEFECTIVE".equalsIgnoreCase(item.getCondition()))) {
                hasDiscrepancy = true;
            }
        }

        if (hasDiscrepancy) {
            return "DISCREPANCY";
        } else if (hasPartial) {
            return "PARTIAL";
        } else {
            return "COMPLETED";
        }
    }

    /**
     * Get all GRNs
     * @return List of all GRNs
     */
    public List<GoodsReceiptNote> getAllGRNs() {
        return grnRepository.findAll();
    }

    /**
     * Get a specific GRN by ID
     * @param id The GRN ID
     * @return Optional containing the GRN if found
     */
    public Optional<GoodsReceiptNote> getGRNById(String id) {
        return grnRepository.findById(id);
    }

    /**
     * Get all GRNs for a specific Purchase Order
     * @param purchaseOrderId The Purchase Order ID
     * @return List of GRNs for the PO
     */
    public List<GoodsReceiptNote> getGRNsByPurchaseOrder(String purchaseOrderId) {
        return grnRepository.findByPurchaseOrderId(purchaseOrderId);
    }

    /**
     * Get all GRNs for a specific Supplier
     * @param supplierId The Supplier ID
     * @return List of GRNs for the supplier
     */
    public List<GoodsReceiptNote> getGRNsBySupplier(String supplierId) {
        return grnRepository.findBySupplierId(supplierId);
    }

    /**
     * Get all GRNs by status
     * @param status The status to filter by
     * @return List of GRNs with the specified status
     */
    public List<GoodsReceiptNote> getGRNsByStatus(String status) {
        return grnRepository.findByStatus(status);
    }

    /**
     * Delete a GRN (typically only used for corrections)
     * Note: This does NOT reverse inventory changes
     * @param id The GRN ID to delete
     */
    public void deleteGRN(String id) {
        grnRepository.deleteById(id);
    }
}
