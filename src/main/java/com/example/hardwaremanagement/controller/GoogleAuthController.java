package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.*;
import com.example.hardwaremanagement.dto.ErrorResponse;
import com.example.hardwaremanagement.dto.GoogleAuthRequest;
import com.example.hardwaremanagement.dto.GoogleAuthResponse;
import com.example.hardwaremanagement.dto.UserResponse;
import com.example.hardwaremanagement.model.User;
import com.example.hardwaremanagement.repository.UserRepository;
import com.example.hardwaremanagement.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.example.hardwaremanagement.service.GoogleTokenVerifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class GoogleAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GoogleTokenVerifierService tokenVerifierService;

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequest request) {
        try {
            GoogleIdToken.Payload payload = tokenVerifierService.verify(request.getIdToken());
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String googleId = payload.getSubject();
            String picture = (String) payload.get("picture");

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null && "signup".equalsIgnoreCase(request.getType())) {
                user = new User();
                user.setEmail(email);
                user.setUsername(email);
                user.setFullName(name);
                user.setRole("CUSTOMER");
                user.setGoogleAuth(true);
                user.setGoogleId(googleId);
                if (request.getProfile() != null && request.getProfile().getImageUrl() != null) {
                    user.setProfileImage(request.getProfile().getImageUrl());
                } else if (picture != null) {
                    user.setProfileImage(picture);
                }
                user = userRepository.save(user);
            } else if (user == null && "signin".equalsIgnoreCase(request.getType())) {
                throw new RuntimeException("No account found with this Google account");
            }

            // Existing user, ensure google fields are set
            if (user != null) {
                if (user.getGoogleId() == null) user.setGoogleId(googleId);
                if (user.getFullName() == null && name != null) user.setFullName(name);
                if (user.getProfileImage() == null && picture != null) user.setProfileImage(picture);
                if (Boolean.FALSE.equals(user.getGoogleAuth())) user.setGoogleAuth(true);
                userRepository.save(user);
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(new GoogleAuthResponse(
                    token,
                    new UserResponse(
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole(),
                            user.getFullName(),
                            user.getProfileImage()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Google authentication failed: " + e.getMessage()));
        }
    }
}
