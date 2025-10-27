package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.PaymentTransactionRequest;
import com.example.hardwaremanagement.dto.PaymentTransactionResponse;
import com.example.hardwaremanagement.model.PaymentTransaction;
import com.example.hardwaremanagement.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments/transactions")
@CrossOrigin(origins = "*")
public class PaymentTransactionController {

    @Autowired
    private PaymentTransactionService paymentTransactionService;

    @PostMapping
    public ResponseEntity<PaymentTransactionResponse> verifyAndLog(@RequestBody PaymentTransactionRequest request) {
        PaymentTransactionResponse response = paymentTransactionService.verifyAndLog(request);
        HttpStatus status = response.isVerified() ? HttpStatus.CREATED : HttpStatus.ACCEPTED;
        return new ResponseEntity<>(response, status);
    }

    @GetMapping
    public ResponseEntity<List<PaymentTransaction>> listTransactions(@RequestParam(value = "orderId", required = false) String orderId,
                                                                     @RequestParam(value = "customerReference", required = false) String customerReference,
                                                                     @RequestParam(value = "paymentIntentId", required = false) String paymentIntentId) {
        if (StringUtils.hasText(orderId)) {
            return ResponseEntity.ok(paymentTransactionService.listByOrderId(orderId));
        }
        if (StringUtils.hasText(paymentIntentId)) {
            return ResponseEntity.ok(paymentTransactionService.listByPaymentIntentId(paymentIntentId));
        }
        if (StringUtils.hasText(customerReference)) {
            return ResponseEntity.ok(paymentTransactionService.listByCustomerReference(customerReference));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransaction> getById(@PathVariable("id") String id) {
        return paymentTransactionService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
