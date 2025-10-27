package com.example.hardwaremanagement.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.lang.NonNull;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailNotificationService {


    private final JavaMailSender mailSender;


    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public String send(@NonNull String to, @NonNull String subject, @NonNull String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // HTML enabled


        // From is configured via spring.mail.username; if not, let mail server default apply
        try {
            mailSender.send(message);
            // A simple id; in production you might use DB id or Message-ID header
            return String.valueOf(System.currentTimeMillis());
        } catch (MailException ex) {
            throw ex; // Let controller convert to 5xx
        }
    }
}




