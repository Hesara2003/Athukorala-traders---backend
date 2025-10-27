package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepo extends MongoRepository<Invoice, String> {
}
