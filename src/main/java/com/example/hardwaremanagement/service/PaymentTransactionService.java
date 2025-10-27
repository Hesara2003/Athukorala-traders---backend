package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.PaymentTransactionRequest;
import com.example.hardwaremanagement.dto.PaymentTransactionResponse;
import com.example.hardwaremanagement.model.PaymentTransaction;
import com.example.hardwaremanagement.model.PaymentTransactionStatus;
import com.example.hardwaremanagement.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentTransactionService {

    @Autowired
    private PaymentTransactionRepository repository;

    public PaymentTransactionResponse verifyAndLog(PaymentTransactionRequest request) {
        List<String> validationErrors = validate(request);
        PaymentTransaction transaction = buildTransaction(request, validationErrors);
        LocalDateTime now = LocalDateTime.now();
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(now);
        }
        transaction.setUpdatedAt(now);

        boolean verified = validationErrors.isEmpty();
        transaction.setVerified(verified);
        transaction.setVerifiedAt(verified ? now : null);
        transaction.setValidationErrors(verified ? null : new ArrayList<>(validationErrors));

        PaymentTransaction saved = repository.save(transaction);

        PaymentTransactionResponse response = new PaymentTransactionResponse();
        response.setVerified(verified);
        response.setMessage(verified ? "Transaction verified successfully." : "Transaction recorded with validation warnings.");
        response.setValidationErrors(validationErrors.isEmpty() ? null : new ArrayList<>(validationErrors));
        response.setTransaction(saved);
        return response;
    }

    public List<PaymentTransaction> listByOrderId(String orderId) {
        if (!StringUtils.hasText(orderId)) {
            return Collections.emptyList();
        }
        return repository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    public List<PaymentTransaction> listByCustomerReference(String reference) {
        if (!StringUtils.hasText(reference)) {
            return Collections.emptyList();
        }
        return repository.findByCustomerReferenceOrderByCreatedAtDesc(reference);
    }

    public List<PaymentTransaction> listByPaymentIntentId(String paymentIntentId) {
        if (!StringUtils.hasText(paymentIntentId)) {
            return Collections.emptyList();
        }
        return repository.findByPaymentIntentIdOrderByCreatedAtDesc(paymentIntentId);
    }

    public Optional<PaymentTransaction> getById(String id) {
        return repository.findById(id);
    }

    private List<String> validate(PaymentTransactionRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request payload is required.");
            return errors;
        }
        if (!StringUtils.hasText(request.getPaymentIntentId())) {
            errors.add("paymentIntentId is required.");
        }
        if (!StringUtils.hasText(request.getPaymentMethod())) {
            errors.add("paymentMethod is required.");
        }
        PaymentTransactionStatus status = parseStatus(request.getStatus());
        if (status == null) {
            errors.add("status must be one of AUTHORIZED, DECLINED, COMPLETED, or PENDING.");
        } else if ((status == PaymentTransactionStatus.AUTHORIZED || status == PaymentTransactionStatus.COMPLETED)
                && (request.getAmount() == null || request.getAmount() <= 0)) {
            errors.add("amount must be greater than zero for successful transactions.");
        }
        if (!StringUtils.hasText(request.getCurrency())) {
            errors.add("currency is required.");
        }
        return errors;
    }

    private PaymentTransaction buildTransaction(PaymentTransactionRequest request, List<String> validationErrors) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPaymentIntentId(resolvePaymentIntentId(request.getPaymentIntentId()));
        transaction.setOrderId(safeTrim(request.getOrderId()));
        transaction.setCustomerId(safeTrim(request.getCustomerId()));
        transaction.setCustomerReference(safeTrim(request.getCustomerReference()));
        transaction.setCustomerEmail(safeTrim(request.getCustomerEmail()));
        transaction.setPaymentMethod(safeTrim(request.getPaymentMethod()));
        transaction.setStatus(resolveStatus(request.getStatus()));
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(resolveCurrency(request.getCurrency()));
        transaction.setGatewayMessage(safeTrim(request.getGatewayMessage()));
        transaction.setFailureReason(safeTrim(request.getFailureReason()));
        transaction.setMetadata(normalizeMetadata(request.getMetadata()));
        if (!validationErrors.isEmpty()) {
            transaction.setValidationErrors(new ArrayList<>(validationErrors));
        }
        return transaction;
    }

    private String resolvePaymentIntentId(String paymentIntentId) {
        if (StringUtils.hasText(paymentIntentId)) {
            return paymentIntentId.trim();
        }
        return "auto-" + UUID.randomUUID();
    }

    private PaymentTransactionStatus resolveStatus(String status) {
        PaymentTransactionStatus parsed = parseStatus(status);
        return parsed != null ? parsed : PaymentTransactionStatus.DECLINED;
    }

    private PaymentTransactionStatus parseStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        try {
            return PaymentTransactionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String resolveCurrency(String currency) {
        if (!StringUtils.hasText(currency)) {
            return null;
        }
        return currency.trim().toUpperCase();
    }

    private String safeTrim(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private Map<String, Object> normalizeMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        return metadata;
    }
}
