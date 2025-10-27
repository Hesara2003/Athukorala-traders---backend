package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.User;
import com.example.hardwaremanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @PutMapping("/users/{id}/role")
    public Map<String, String> updateUserRole(@PathVariable String id, @RequestBody Map<String, String> body) {
        String newRole = body.get("role");
        Map<String, String> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            userRepository.save(user);
            response.put("message", "User role updated successfully");
            response.put("username", user.getUsername());
            response.put("newRole", user.getRole());
        } else {
            response.put("error", "User not found");
        }

        return response;
    }
}
