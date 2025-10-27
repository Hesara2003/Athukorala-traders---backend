package com.example.hardwaremanagement.dto;

import com.example.hardwaremanagement.model.PaymentTransaction;

import java.util.List;

public class PaymentTransactionResponse {
    private boolean verified;
    private String message;
    private List<String> validationErrors;
    private PaymentTransaction transaction;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public PaymentTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(PaymentTransaction transaction) {
        this.transaction = transaction;
    }
}
