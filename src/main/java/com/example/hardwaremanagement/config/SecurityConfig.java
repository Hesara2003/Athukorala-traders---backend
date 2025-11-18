package com.example.hardwaremanagement.config;


import com.example.hardwaremanagement.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // enable CORS
                .csrf(csrf -> csrf.disable())  // disable CSRF for REST API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/customer/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/files/**").permitAll()  // Allow file access
                        .requestMatchers("/uploads/**").permitAll()     // Allow static file access
                        
                        // Shift management endpoints - permitAll for demo/testing
                        .requestMatchers("/api/shifts/**").permitAll()
                        
                        // Fulfillment endpoints - permitAll for demo/testing
                        .requestMatchers("/api/fulfillment/**").permitAll()
                        
                        // Supplier endpoints - permitAll for demo/testing
                        .requestMatchers("/api/suppliers/**").permitAll()
                        
                        // Supplier purchase order endpoints - permitAll for demo/testing
                        .requestMatchers("/api/supplier/purchase-orders/**").permitAll()
                        
                        // Audit log endpoints - permitAll for demo/testing
                        .requestMatchers("/api/audit-logs/**").permitAll()
                        
                        // Inventory sync endpoints - permitAll for demo/testing
                        .requestMatchers("/api/inventory/sync/**").permitAll()
                        
                        // Inventory management endpoints
                        .requestMatchers("/api/inventory/**").permitAll()
                        
                        // Reviews endpoints
                        .requestMatchers("/api/reviews/**").permitAll()
                        
                        // Coupons endpoints
                        .requestMatchers("/api/coupons/**").permitAll()
                        
                        // Returns endpoints
                        .requestMatchers("/api/returns/**").permitAll()
                        
                        // Price management endpoints - permitAll for demo/testing
                        .requestMatchers("/api/prices/**").permitAll()
                        
                        // Admin endpoints - require authentication (specific roles via @PreAuthorize)
                        .requestMatchers("/api/admin/**").authenticated()
                        
                        // Supplier endpoints - require authentication
                        .requestMatchers("/api/supplier/**").authenticated()
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", 
            "http://localhost:5177",
            "http://localhost:3000",
            "https://athukorala-traders-frontend.vercel.app"
        )); // Allow frontend origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
