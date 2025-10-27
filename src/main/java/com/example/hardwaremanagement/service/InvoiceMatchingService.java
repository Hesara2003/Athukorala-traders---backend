package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.InvoiceMatchResult;
import com.example.hardwaremanagement.dto.ParsedInvoiceLine;
import com.example.hardwaremanagement.model.Invoice;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.model.PurchaseOrder;
import com.example.hardwaremanagement.model.PurchaseOrderItem;
import com.example.hardwaremanagement.repository.InvoiceRepo;
import com.example.hardwaremanagement.repository.ProductRepository;
import com.example.hardwaremanagement.repository.PurchaseOrderRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class InvoiceMatchingService {
    @Autowired
    private PurchaseOrderRepository poRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private InvoiceRepo invoiceRepo;

    public InvoiceMatchResult uploadAndMatch(String poId, MultipartFile file) throws IOException {
        PurchaseOrder po = poRepo.findById(poId).orElseThrow(() -> new IllegalArgumentException("PO not found: " + poId));

        List<ParsedInvoiceLine> lines = parseCsv(file);

        Map<String, Integer> poQty = new HashMap<>();
        if (po.getItems() != null) {
            for (PurchaseOrderItem it : po.getItems()) {
                poQty.merge(it.getProductId(), it.getQuantity(), Integer::sum);
            }
        }

        Map<String, Product> productMap = new HashMap<>();
        // Preload products referenced by either PO or invoice lines
        Set<String> allProductIds = new HashSet<>();
        allProductIds.addAll(poQty.keySet());
        for (ParsedInvoiceLine l : lines) allProductIds.add(l.getProductId());
        if (!allProductIds.isEmpty()) {
            productRepo.findAllById(allProductIds).forEach(p -> productMap.put(p.getId(), p));
        }

        InvoiceMatchResult result = new InvoiceMatchResult();
        result.setPoId(poId);

        double invoiceTotal = 0.0;
        double expectedTotal = 0.0;

        // Expected total from PO using current product prices (if no item price on PO)
        for (Map.Entry<String, Integer> e : poQty.entrySet()) {
            Product p = productMap.get(e.getKey());
            if (p != null) expectedTotal += p.getPrice() * e.getValue();
        }

        // Validate lines
        Map<String, Integer> invoiceQty = new HashMap<>();
        for (ParsedInvoiceLine l : lines) {
            invoiceTotal += l.getUnitPrice() * l.getQuantity();
            invoiceQty.merge(l.getProductId(), l.getQuantity(), Integer::sum);

            if (!poQty.containsKey(l.getProductId())) {
                result.getIssues().add(new InvoiceMatchResult.LineIssue(l.getProductId(), "Product not in PO"));
            } else {
                int expectedQ = poQty.get(l.getProductId());
                if (l.getQuantity() != expectedQ) {
                    result.getIssues().add(new InvoiceMatchResult.LineIssue(l.getProductId(), "Quantity mismatch: expected " + expectedQ + ", got " + l.getQuantity()));
                }
                Product p = productMap.get(l.getProductId());
                if (p != null) {
                    if (Math.abs(p.getPrice() - l.getUnitPrice()) > 0.001) {
                        result.getIssues().add(new InvoiceMatchResult.LineIssue(l.getProductId(), String.format(Locale.US, "Unit price mismatch: expected %.2f, got %.2f", p.getPrice(), l.getUnitPrice())));
                    }
                } else {
                    result.getIssues().add(new InvoiceMatchResult.LineIssue(l.getProductId(), "Unknown product - cannot verify price"));
                }
            }
        }

        // Check for missing products that are in PO but not in invoice
        for (String pid : poQty.keySet()) {
            if (!invoiceQty.containsKey(pid)) {
                result.getIssues().add(new InvoiceMatchResult.LineIssue(pid, "Missing on invoice"));
            }
        }

        result.setInvoiceTotal(invoiceTotal);
        result.setExpectedTotal(expectedTotal);
        result.setMatch(result.getIssues().isEmpty() && Math.abs(invoiceTotal - expectedTotal) < 0.01);

        // Save a minimal invoice record for tracking
        Invoice inv = new Invoice();
        inv.setPurchaseOrderId(poId);
        inv.setSupplierId(po.getSupplierId());
        inv.setAmount(invoiceTotal);
        inv.setIssuedAt(LocalDateTime.now());
        Invoice saved = invoiceRepo.save(inv);
        result.setSavedInvoiceId(saved.getId());

        return result;
    }

    private List<ParsedInvoiceLine> parseCsv(MultipartFile file) throws IOException {
        List<ParsedInvoiceLine> lines = new ArrayList<>();
    CSVFormat format = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .setIgnoreEmptyLines(true)
        .build();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
         CSVParser parser = format.parse(reader)) {
            for (CSVRecord r : parser) {
                String productId = r.get("productId");
                int quantity = Integer.parseInt(r.get("quantity"));
                double unitPrice = Double.parseDouble(r.get("unitPrice"));
                lines.add(new ParsedInvoiceLine(productId, quantity, unitPrice));
            }
        }
        return lines;
    }
}
