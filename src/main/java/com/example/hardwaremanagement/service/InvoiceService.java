package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.Invoice;
import com.example.hardwaremanagement.repository.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepo invoiceRepository;

    public Invoice createInvoice(Invoice invoice) {
        invoice.setIssuedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}
