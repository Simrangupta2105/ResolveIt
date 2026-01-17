package com.resolve.complaint.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Complaint Portal API is running");
        response.put("status", "OK");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "auth", "/api/auth",
            "complaints", "/api/complaints",
            "employee-requests", "/api/employee-requests",
            "h2-console", "/h2-console"
        ));
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "complaint-portal");
        return response;
    }
}