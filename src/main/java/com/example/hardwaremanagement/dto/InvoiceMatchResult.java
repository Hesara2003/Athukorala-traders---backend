package com.example.hardwaremanagement.dto;

import java.util.ArrayList;
import java.util.List;

public class InvoiceMatchResult {
    public static class LineIssue {
        private String productId;
        private String message;

        public LineIssue() {}

        public LineIssue(String productId, String message) {
            this.productId = productId;
            this.message = message;
        }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    private String poId;
    private boolean match;
    private double expectedTotal;
    private double invoiceTotal;
    private List<LineIssue> issues = new ArrayList<>();
    private String savedInvoiceId;

    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public boolean isMatch() { return match; }
    public void setMatch(boolean match) { this.match = match; }
    public double getExpectedTotal() { return expectedTotal; }
    public void setExpectedTotal(double expectedTotal) { this.expectedTotal = expectedTotal; }
    public double getInvoiceTotal() { return invoiceTotal; }
    public void setInvoiceTotal(double invoiceTotal) { this.invoiceTotal = invoiceTotal; }
    public List<LineIssue> getIssues() { return issues; }
    public void setIssues(List<LineIssue> issues) { this.issues = issues; }
    public String getSavedInvoiceId() { return savedInvoiceId; }
    public void setSavedInvoiceId(String savedInvoiceId) { this.savedInvoiceId = savedInvoiceId; }
}
