package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.CheckoutDetailsRequest;
import com.example.hardwaremanagement.dto.CheckoutDetailsResponse;
import com.example.hardwaremanagement.service.CheckoutDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.Map;

@RestController
@RequestMapping("/api/customer/checkout-details")
@CrossOrigin(origins = "*")
public class CheckoutDetailsController {

    @Autowired
    private CheckoutDetailsService checkoutDetailsService;

    @PostMapping
    public ResponseEntity<?> saveCheckoutDetails(@RequestBody CheckoutDetailsRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Request body is required."));
        }
        if (!StringUtils.hasText(request.getCustomerId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Customer ID is required."));
        }
        if (request.getBilling() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Billing information is required."));
        }

        CheckoutDetailsResponse response = checkoutDetailsService.saveDetails(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
