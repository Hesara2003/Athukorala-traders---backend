package com.example.hardwaremanagement.controller;


import com.example.hardwaremanagement.dto.NotificationRequest;
import com.example.hardwaremanagement.service.EmailNotificationService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationsController {


    private final EmailNotificationService emailService;


    public NotificationsController(EmailNotificationService emailService) {
        this.emailService = emailService;
    }


    @PostMapping
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequest request) {
        if (request.getRecipient() == null || request.getRecipient().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("recipient is required");
        }
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("subject is required");
        }
        if (request.getBody() == null || request.getBody().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("body is required");
        }


        try {
            String id = emailService.send(request.getRecipient(), request.getSubject(), request.getBody());
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", id);
            resp.put("status", "sent");
            return ResponseEntity.ok(resp);
        } catch (MessagingException | RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}






