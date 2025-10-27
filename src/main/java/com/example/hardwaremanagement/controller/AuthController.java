package com.example.hardwaremanagement.controller;


import com.example.hardwaremanagement.model.User;
import com.example.hardwaremanagement.repository.UserRepository;
import com.example.hardwaremanagement.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> userMap) {
        String username = userMap.get("username");
        String password = userMap.get("password");

        User user = userRepository.findByUsername(username);
        Map<String, String> response = new HashMap<>();

        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            response.put("error", "Invalid username or password");
            return response;
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        if (user.getEmail() != null) {
            response.put("email", user.getEmail());
        }
        return response;
    }

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody Map<String, String> userMap) {
        String username = userMap.get("username");
        String email = userMap.get("email");
        String password = userMap.get("password");
        String role = userMap.get("role"); // e.g., CUSTOMER, ADMIN, STAFF

        Map<String, String> response = new HashMap<>();

        if (userRepository.findByUsername(username) != null) {
            response.put("error", "Username already exists");
            return response;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setRole(role);

        userRepository.save(user);

        response.put("message", "User registered successfully");
        return response;
    }

}

