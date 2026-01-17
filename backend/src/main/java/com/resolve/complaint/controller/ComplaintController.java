package com.resolve.complaint.controller;

import com.resolve.complaint.dto.ComplaintRequest;
import com.resolve.complaint.dto.ComplaintResponse;
import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.model.ComplaintAttachment;
import com.resolve.complaint.security.UserPrincipal;
import com.resolve.complaint.service.ComplaintService;
import com.resolve.complaint.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<?> createComplaint(
            @Valid @RequestPart("complaint") ComplaintRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) {
        
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated() && 
            request.getSubmissionType() == Complaint.SubmissionType.PUBLIC) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userId = userPrincipal.getId();
        }

        ComplaintResponse response = complaintService.createComplaint(request, userId, files);
        
        // For anonymous complaints, return a special response that doesn't expose the full complaint ID
        if (request.getSubmissionType() == Complaint.SubmissionType.ANONYMOUS) {
            Map<String, Object> anonymousResponse = new HashMap<>();
            anonymousResponse.put("message", "Anonymous complaint submitted successfully!");
            anonymousResponse.put("submissionType", "ANONYMOUS");
            anonymousResponse.put("status", "NEW");
            anonymousResponse.put("trackingInfo", "Your complaint has been received and will be processed. You can check the public dashboard for updates on similar complaints.");
            anonymousResponse.put("success", true);
            return ResponseEntity.ok(anonymousResponse);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{complaintId}")
    public ResponseEntity<?> getComplaint(
            @PathVariable String complaintId,
            Authentication authentication) {
        
        // Check if user is admin
        boolean isAdmin = false;
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        }
        
        ComplaintResponse response = complaintService.getComplaintByComplaintId(complaintId);
        
        // Block access to anonymous complaints for non-admin users
        if (response.getSubmissionType() == Complaint.SubmissionType.ANONYMOUS && !isAdmin) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Anonymous complaints cannot be tracked for privacy protection");
            errorResponse.put("message", "This complaint was submitted anonymously and cannot be tracked by ID to protect the submitter's privacy.");
            errorResponse.put("suggestion", "You can view general complaint status updates on the public dashboard.");
            return ResponseEntity.status(403).body(errorResponse);
        }
        
        // Set anonymous flag for frontend handling (for admin users)
        if (response.getSubmissionType() == Complaint.SubmissionType.ANONYMOUS && isAdmin) {
            response.setAnonymous(true);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ComplaintResponse>> getComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        // Check if user is admin
        boolean isAdmin = false;
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ComplaintResponse> complaints = complaintService.getComplaints(pageable);
        
        // For non-admin users, mask anonymous complaint IDs
        if (!isAdmin) {
            complaints = complaints.map(complaint -> {
                if (complaint.getSubmissionType() == Complaint.SubmissionType.ANONYMOUS) {
                    complaint.setAnonymous(true);
                }
                return complaint;
            });
        }
        
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ComplaintResponse>> getMyComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<ComplaintResponse> complaints = complaintService.getComplaintsByUser(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/{complaintId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('SUPERVISOR')")
    public ResponseEntity<ComplaintResponse> updateComplaintStatus(
            @PathVariable String complaintId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Complaint.Status newStatus = Complaint.Status.valueOf((String) request.get("status"));
        String comment = (String) request.get("comment");

        ComplaintResponse response = complaintService.updateComplaintStatus(
            complaintId, newStatus, comment, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    // File download endpoints for admins and employees
    @GetMapping("/{complaintId}/attachments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<ComplaintAttachment>> getComplaintAttachments(
            @PathVariable String complaintId,
            Authentication authentication) {
        
        List<ComplaintAttachment> attachments = complaintService.getComplaintAttachments(complaintId);
        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/{complaintId}/attachments/{attachmentId}/download")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('SUPERVISOR')")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable String complaintId,
            @PathVariable Long attachmentId,
            Authentication authentication) {
        
        try {
            ComplaintAttachment attachment = complaintService.getComplaintAttachment(complaintId, attachmentId);
            
            // Use FileSystemResource for better streaming support
            Resource resource = new FileSystemResource(attachment.getFilePath());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determine content type based on file extension
            String contentType = getContentType(attachment.getFileName());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
                
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{complaintId}/attachments/{attachmentId}/view")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('SUPERVISOR')")
    public ResponseEntity<Resource> viewAttachment(
            @PathVariable String complaintId,
            @PathVariable Long attachmentId,
            Authentication authentication) {
        
        try {
            ComplaintAttachment attachment = complaintService.getComplaintAttachment(complaintId, attachmentId);
            
            // Use FileSystemResource for better streaming support
            Resource resource = new FileSystemResource(attachment.getFilePath());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determine content type for inline viewing
            String contentType = getContentType(attachment.getFileName());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
                
        } catch (Exception e) {
            System.err.println("Error viewing attachment: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{complaintId}/file/{attachmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('SUPERVISOR')")
    public ResponseEntity<Resource> getFile(
            @PathVariable String complaintId,
            @PathVariable Long attachmentId,
            Authentication authentication) {
        
        try {
            ComplaintAttachment attachment = complaintService.getComplaintAttachment(complaintId, attachmentId);
            
            // Handle both absolute and relative paths
            String filePath = attachment.getFilePath();
            Path path;
            
            if (filePath.startsWith("/") || filePath.contains(":")) {
                // Absolute path
                path = Paths.get(filePath);
            } else {
                // Relative path - prepend upload directory
                path = Paths.get("./uploads", filePath);
            }
            
            Resource resource = new FileSystemResource(path);
            
            if (!resource.exists()) {
                System.err.println("File not found at: " + path.toString());
                return ResponseEntity.notFound().build();
            }
            
            String contentType = getContentType(attachment.getFileName());
            
            System.out.println("Serving file: " + attachment.getFileName() + " from: " + path.toString());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
                
        } catch (Exception e) {
            System.err.println("Error retrieving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public/file/{complaintId}/{attachmentId}")
    public ResponseEntity<Resource> getPublicFile(
            @PathVariable String complaintId,
            @PathVariable Long attachmentId) {
        
        try {
            ComplaintAttachment attachment = complaintService.getComplaintAttachment(complaintId, attachmentId);
            
            // Handle both absolute and relative paths
            String filePath = attachment.getFilePath();
            Path path;
            
            if (filePath.startsWith("/") || filePath.contains(":")) {
                // Absolute path
                path = Paths.get(filePath);
            } else {
                // Relative path - prepend upload directory
                path = Paths.get("./uploads", filePath);
            }
            
            Resource resource = new FileSystemResource(path);
            
            if (!resource.exists()) {
                System.err.println("File not found at: " + path.toString());
                return ResponseEntity.notFound().build();
            }
            
            String contentType = getContentType(attachment.getFileName());
            
            System.out.println("Serving public file: " + attachment.getFileName() + " from: " + path.toString());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
                
        } catch (Exception e) {
            System.err.println("Error retrieving public file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        switch (extension) {
            // Images
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            
            // Videos
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "flv":
                return "video/x-flv";
            case "webm":
                return "video/webm";
            case "mkv":
                return "video/x-matroska";
            
            // Documents
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "txt":
                return "text/plain";
            case "rtf":
                return "application/rtf";
            
            default:
                return "application/octet-stream";
        }
    }
}