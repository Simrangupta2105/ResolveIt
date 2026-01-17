package com.resolve.complaint.controller;

import com.resolve.complaint.dto.LoginRequest;
import com.resolve.complaint.security.JwtUtils;
import com.resolve.complaint.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("accessToken", jwt); // Add accessToken for compatibility
            response.put("type", "Bearer");
            response.put("id", userDetails.getId());
            response.put("username", userDetails.getUsername());
            response.put("email", userDetails.getEmail());
            response.put("roles", userDetails.getAuthorities());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signInUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Same as login endpoint for compatibility
        return authenticateUser(loginRequest);
    }

    @GetMapping("/test")
    public ResponseEntity<?> testAuth() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Auth endpoint is working");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}