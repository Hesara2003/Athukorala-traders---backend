package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.GoodsReceiptNote;
import com.example.hardwaremanagement.service.GoodsReceiptNoteService;
import com.example.hardwaremanagement.service.GRNExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for GRN (Goods Receipt Note) operations
 * Provides endpoints for store staff to receive goods against Purchase Orders
 * 
 * Security:
 * - All endpoints require authentication
 * - ADMIN and STAFF roles can perform all operations
 * - Proper JWT token required in Authorization header
 */
@RestController
@RequestMapping("/api/admin/grn")
@CrossOrigin(origins = "*")
public class GoodsReceiptNoteController {

    @Autowired
    private GoodsReceiptNoteService grnService;

    @Autowired
    private GRNExportService exportService;

    /**
     * Create a new GRN - Receive goods against a Purchase Order
     * This endpoint automatically updates inventory when goods are received
     * 
     * POST /api/admin/grn
     * 
     * Request Body:
     * {
     *   "purchaseOrderId": "string",
     *   "receivedBy": "string",
     *   "items": [
     *     {
     *       "productId": "string",
     *       "orderedQuantity": number,
     *       "receivedQuantity": number,
     *       "condition": "GOOD|DAMAGED|DEFECTIVE",
     *       "remarks": "string"
     *     }
     *   ],
     *   "notes": "string"
     * }
     * 
     * @param grn The GRN details
     * @return The created GRN with updated inventory
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> createGRN(@RequestBody GoodsReceiptNote grn) {
        try {
            // Get authenticated user info
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();
            
            // If receivedBy is not provided, use authenticated user
            if (grn.getReceivedBy() == null || grn.getReceivedBy().isEmpty()) {
                grn.setReceivedBy(authenticatedUser);
            }
            
            // Validate required fields
            if (grn.getPurchaseOrderId() == null || grn.getPurchaseOrderId().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Purchase Order ID is required"));
            }

            if (grn.getReceivedBy() == null || grn.getReceivedBy().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Received By (staff member) is required"));
            }

            if (grn.getItems() == null || grn.getItems().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("At least one item must be received"));
            }

            // Create GRN and update inventory
            GoodsReceiptNote createdGRN = grnService.createGRN(grn);
            
            return new ResponseEntity<>(createdGRN, HttpStatus.CREATED);
            
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error creating GRN: " + e.getMessage()));
        }
    }

    /**
     * Get all GRNs
     * GET /api/admin/grn
     * 
     * @return List of all GRNs
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<GoodsReceiptNote>> getAllGRNs() {
        List<GoodsReceiptNote> grns = grnService.getAllGRNs();
        return ResponseEntity.ok(grns);
    }

    /**
     * Get a specific GRN by ID
     * GET /api/admin/grn/{id}
     * 
     * @param id The GRN ID
     * @return The GRN details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getGRNById(@PathVariable String id) {
        return grnService.getGRNById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("GRN not found: " + id)));
    }

    /**
     * Get all GRNs for a specific Purchase Order
     * GET /api/admin/grn/purchase-order/{purchaseOrderId}
     * 
     * @param purchaseOrderId The Purchase Order ID
     * @return List of GRNs for the PO
     */
    @GetMapping("/purchase-order/{purchaseOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<GoodsReceiptNote>> getGRNsByPurchaseOrder(@PathVariable String purchaseOrderId) {
        List<GoodsReceiptNote> grns = grnService.getGRNsByPurchaseOrder(purchaseOrderId);
        return ResponseEntity.ok(grns);
    }

    /**
     * Get all GRNs for a specific Supplier
     * GET /api/admin/grn/supplier/{supplierId}
     * 
     * @param supplierId The Supplier ID
     * @return List of GRNs for the supplier
     */
    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<GoodsReceiptNote>> getGRNsBySupplier(@PathVariable String supplierId) {
        List<GoodsReceiptNote> grns = grnService.getGRNsBySupplier(supplierId);
        return ResponseEntity.ok(grns);
    }

    /**
     * Get all GRNs by status
     * GET /api/admin/grn/status/{status}
     * 
     * @param status The status to filter by (COMPLETED, PARTIAL, DISCREPANCY)
     * @return List of GRNs with the specified status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<GoodsReceiptNote>> getGRNsByStatus(@PathVariable String status) {
        List<GoodsReceiptNote> grns = grnService.getGRNsByStatus(status);
        return ResponseEntity.ok(grns);
    }

    /**
     * Delete a GRN
     * DELETE /api/admin/grn/{id}
     * Note: This does NOT reverse inventory changes
     * 
     * @param id The GRN ID to delete
     * @return No content on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGRN(@PathVariable String id) {
        grnService.deleteGRN(id);
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // Export Endpoints (PDF & Excel)
    // ========================================



    /**
     * Export a single GRN to PDF
     * GET /api/admin/grn/{id}/pdf
     * 
     * @param id The GRN ID to export
     * @return PDF file as byte array
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportGRNToPdf(@PathVariable String id) {
        try {
            GoodsReceiptNote grn = grnService.getGRNById(id)
                    .orElseThrow(() -> new RuntimeException("GRN not found with id: " + id));

            byte[] pdfBytes = exportService.generateGRNPdf(grn);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "GRN_" + id + "_" + getCurrentTimestamp() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export multiple GRNs to Excel
     * GET /api/admin/grn/export/excel
     * Optional query parameters: status, supplierId, purchaseOrderId
     * 
     * @param status Optional filter by status
     * @param supplierId Optional filter by supplier
     * @param purchaseOrderId Optional filter by purchase order
     * @return Excel file as byte array
     */
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportGRNsToExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplierId,
            @RequestParam(required = false) String purchaseOrderId) {
        try {
            List<GoodsReceiptNote> grns;

            // Apply filters
            if (purchaseOrderId != null) {
                grns = grnService.getGRNsByPurchaseOrder(purchaseOrderId);
            } else if (supplierId != null) {
                grns = grnService.getGRNsBySupplier(supplierId);
            } else if (status != null) {
                grns = grnService.getGRNsByStatus(status);
            } else {
                grns = grnService.getAllGRNs();
            }

            byte[] excelBytes = exportService.generateGRNExcel(grns);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                "GRN_Report_" + getCurrentTimestamp() + ".xlsx");
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export a single GRN to detailed Excel with multiple sheets
     * GET /api/admin/grn/{id}/excel
     * 
     * @param id The GRN ID to export
     * @return Excel file as byte array
     */
    @GetMapping("/{id}/excel")
    public ResponseEntity<byte[]> exportGRNToDetailedExcel(@PathVariable String id) {
        try {
            GoodsReceiptNote grn = grnService.getGRNById(id)
                    .orElseThrow(() -> new RuntimeException("GRN not found with id: " + id));

            byte[] excelBytes = exportService.generateDetailedGRNExcel(grn);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                "GRN_Details_" + id + "_" + getCurrentTimestamp() + ".xlsx");
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    /**
     * Helper method to get current timestamp for filenames
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}
