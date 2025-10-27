package com.example.hardwaremanagement.dto;


public class NotificationRequest {
    private String type;      // ORDER_STATUS, DELIVERY_UPDATE, GENERAL
    private String recipient; // email address
    private String subject;
    private String body;      // HTML content allowed


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getRecipient() {
        return recipient;
    }


    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }
}




