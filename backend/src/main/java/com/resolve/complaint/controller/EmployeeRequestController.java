package com.resolve.complaint.controller;

import com.resolve.complaint.model.EmployeeRequest;
import com.resolve.complaint.repository.EmployeeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/employee-requests")
public class EmployeeRequestController {

    @Autowired
    private EmployeeRequestRepository employeeRequestRepository;

    @PostMapping
    public ResponseEntity<EmployeeRequest> createEmployeeRequest(@Valid @RequestBody EmployeeRequest request) {
        EmployeeRequest savedRequest = employeeRequestRepository.save(request);
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EmployeeRequest>> getEmployeeRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeRequest> requests;
        
        if (status != null) {
            EmployeeRequest.Status requestStatus = EmployeeRequest.Status.valueOf(status.toUpperCase());
            requests = employeeRequestRepository.findByStatusOrderByRequestedAtDesc(requestStatus, pageable);
        } else {
            requests = employeeRequestRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeRequest> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        EmployeeRequest employeeRequest = employeeRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee request not found"));
        
        EmployeeRequest.Status newStatus = EmployeeRequest.Status.valueOf(request.get("status").toUpperCase());
        employeeRequest.setStatus(newStatus);
        
        EmployeeRequest updatedRequest = employeeRequestRepository.save(employeeRequest);
        return ResponseEntity.ok(updatedRequest);
    }
}