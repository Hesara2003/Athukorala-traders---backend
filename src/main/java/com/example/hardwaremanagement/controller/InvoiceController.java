package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.InvoiceMatchResult;
import com.example.hardwaremanagement.model.Invoice;
import com.example.hardwaremanagement.service.InvoiceService;
import com.example.hardwaremanagement.service.InvoiceMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceMatchingService matchingService;

    // Create new invoice
    @PostMapping("/invoices")
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }

    // Get invoice by ID
    @GetMapping("/invoices/{id}")
    public Invoice getInvoice(@PathVariable String id) {
        return invoiceService.getInvoiceById(id);
    }
    @GetMapping("/invoices/all")
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    // Upload invoice file (CSV) and match with Purchase Order
    @PostMapping(value = "/invoices/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public InvoiceMatchResult uploadAndMatch(@RequestParam("poId") String poId,
                                             @RequestPart("file") MultipartFile file,
                                             @RequestPart(value = "metadata", required = false) String metadata) throws Exception {
        return matchingService.uploadAndMatch(poId, file);
    }
}
