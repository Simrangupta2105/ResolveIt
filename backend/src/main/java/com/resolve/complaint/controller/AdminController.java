package com.resolve.complaint.controller;

import com.resolve.complaint.dto.ComplaintResponse;
import com.resolve.complaint.dto.DashboardStatsResponse;
import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.model.User;
import com.resolve.complaint.repository.ComplaintRepository;
import com.resolve.complaint.security.UserPrincipal;
import com.resolve.complaint.service.AdminService;
import com.resolve.complaint.service.ComplaintService;
import com.resolve.complaint.service.UserService;
import com.resolve.complaint.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('SUPERVISOR')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ComplaintRepository complaintRepository;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/complaints")
    public ResponseEntity<Page<ComplaintResponse>> getFilteredComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ComplaintResponse> complaints = adminService.getFilteredComplaints(
            pageable, status, category, priority, assignedTo, search);
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/complaints/{complaintId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignComplaint(
            @PathVariable String complaintId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = request.get("userId") != null ? 
            Long.valueOf(request.get("userId").toString()) : null;
        
        ComplaintResponse response = adminService.assignComplaint(
            complaintId, userId, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complaints/{complaintId}/escalate")
    public ResponseEntity<?> escalateComplaint(
            @PathVariable String complaintId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long higherAuthorityId = request.get("higherAuthorityId") != null ? 
            Long.valueOf(request.get("higherAuthorityId").toString()) : null;
        String reason = (String) request.get("reason");
        boolean notifyAllParties = Boolean.parseBoolean(
            request.getOrDefault("notifyAllParties", "false").toString());
        
        ComplaintResponse response = adminService.escalateComplaint(
            complaintId, higherAuthorityId, reason, notifyAllParties, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complaints/{complaintId}/notes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addInternalNote(
            @PathVariable String complaintId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String note = (String) request.get("note");
        boolean isPublic = Boolean.parseBoolean(
            request.getOrDefault("isPublic", "false").toString());
        
        ComplaintResponse response = adminService.addComplaintNote(
            complaintId, note, isPublic, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complaints/{complaintId}/private-notes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPrivateNote(
            @PathVariable String complaintId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String note = (String) request.get("note");
        
        ComplaintResponse response = adminService.addPrivateNote(
            complaintId, note, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/assignable-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAssignableUsers() {
        List<User> assignableUsers = userService.getAssignableUsers();
        return ResponseEntity.ok(assignableUsers);
    }

    @GetMapping("/reports/complaints")
    public ResponseEntity<?> generateComplaintReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "json") String format) {
        
        Map<String, Object> report = adminService.generateComplaintReport(
            startDate, endDate, category, status, format);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/trends")
    public ResponseEntity<?> getComplaintTrends(
            @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Object> trends = adminService.getComplaintTrends(days);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/escalations")
    public ResponseEntity<Page<ComplaintResponse>> getEscalatedComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ComplaintResponse> escalatedComplaints = adminService.getEscalatedComplaints(pageable);
        return ResponseEntity.ok(escalatedComplaints);
    }

    @GetMapping("/performance")
    public ResponseEntity<?> getPerformanceMetrics() {
        Map<String, Object> metrics = adminService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/reports/export/csv")
    public ResponseEntity<byte[]> exportComplaintsCSV(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        
        byte[] csvData = exportService.generateComplaintReportCSV(startDate, endDate, category, status);
        
        String filename = "complaints_report_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
            ".csv";
        
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + filename)
            .header("Content-Type", "text/csv")
            .body(csvData);
    }

    @GetMapping("/reports/export/pdf")
    public ResponseEntity<byte[]> exportComplaintsPDF(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        
        byte[] pdfData = exportService.generateComplaintReportPDF(startDate, endDate, category, status);
        
        String filename = "complaints_report_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
            ".pdf";
        
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + filename)
            .header("Content-Type", "application/pdf")
            .body(pdfData);
    }
}