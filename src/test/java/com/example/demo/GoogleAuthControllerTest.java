package com.example.demo;

import com.example.hardwaremanagement.controller.GoogleAuthController;
import com.example.hardwaremanagement.dto.GoogleAuthRequest;
import com.example.hardwaremanagement.dto.GoogleProfile;
import com.example.hardwaremanagement.repository.UserRepository;
import com.example.hardwaremanagement.security.JwtUtil;
import com.example.hardwaremanagement.service.GoogleTokenVerifierService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class GoogleAuthControllerTest {

    @Test
    void signup_createsUser_andReturnsToken() throws Exception {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        JwtUtil jwtUtil = new JwtUtil();
        GoogleTokenVerifierService verifier = Mockito.mock(GoogleTokenVerifierService.class);

        GoogleAuthController controller = new GoogleAuthController();
        // inject mocks via reflection since no setters
        try {
            var f1 = GoogleAuthController.class.getDeclaredField("userRepository");
            f1.setAccessible(true);
            f1.set(controller, userRepository);
            var f2 = GoogleAuthController.class.getDeclaredField("jwtUtil");
            f2.setAccessible(true);
            f2.set(controller, jwtUtil);
            var f3 = GoogleAuthController.class.getDeclaredField("tokenVerifierService");
            f3.setAccessible(true);
            f3.set(controller, verifier);
        } catch (Exception e) { fail(e); }

        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("newuser@example.com");
        payload.setSubject("google-123");
        payload.set("name", "New User");
        payload.set("picture", "http://example.com/pic.jpg");

        Mockito.when(verifier.verify(anyString())).thenReturn(payload);
        Mockito.when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        GoogleAuthRequest req = new GoogleAuthRequest();
        req.setIdToken("dummy");
        req.setType("signup");
        GoogleProfile profile = new GoogleProfile();
        profile.setImageUrl("http://example.com/pic.jpg");
        req.setProfile(profile);

        ResponseEntity<?> resp = controller.authenticateWithGoogle(req);
        assertEquals(200, resp.getStatusCode().value());
    }
}
