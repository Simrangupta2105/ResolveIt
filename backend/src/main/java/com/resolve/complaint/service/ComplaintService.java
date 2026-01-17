package com.resolve.complaint.service;

import com.resolve.complaint.dto.ComplaintRequest;
import com.resolve.complaint.dto.ComplaintResponse;
import com.resolve.complaint.model.*;
import com.resolve.complaint.repository.ComplaintRepository;
import com.resolve.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WebSocketService webSocketService;

    public ComplaintResponse createComplaint(ComplaintRequest request, Long userId, List<MultipartFile> files) {
        // Generate complaint ID
        String complaintId = generateComplaintId();

        Complaint complaint = new Complaint();
        complaint.setComplaintId(complaintId);
        complaint.setSubject(request.getSubject());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setPriority(request.getPriority());
        complaint.setSubmissionType(request.getSubmissionType());

        if (userId != null && request.getSubmissionType() == Complaint.SubmissionType.PUBLIC) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            complaint.setUser(user);
        }

        complaint = complaintRepository.save(complaint);

        // Handle file uploads
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filePath = fileStorageService.storeFile(file, complaint.getId());
                    ComplaintAttachment attachment = new ComplaintAttachment(
                            complaint, file.getOriginalFilename(), filePath,
                            file.getSize(), file.getContentType());
                    complaint.getAttachments().add(attachment);
                }
            }
        }

        // Create initial status update
        User systemUser = userRepository.findById(1L).orElse(null); // Admin user
        if (systemUser != null) {
            ComplaintUpdate initialUpdate = new ComplaintUpdate(
                    complaint, Complaint.Status.NEW,
                    "Complaint submitted successfully", systemUser);
            complaint.getUpdates().add(initialUpdate);
        }

        complaint = complaintRepository.save(complaint);

        ComplaintResponse response = mapToResponse(complaint);

        // Send real-time notification for new complaint
        webSocketService.notifyNewComplaint(response);

        return response;
    }

    public ComplaintResponse updateComplaintStatus(String complaintId, Complaint.Status newStatus,
            String comment, Long updatedById) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📝 STATUS UPDATE INITIATED");
        System.out.println("=".repeat(80));
        System.out.println("Complaint ID: " + complaintId);
        System.out.println("New Status: " + newStatus);
        System.out.println("Updated By ID: " + updatedById);

        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User updatedBy = userRepository.findById(updatedById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store old status for notification
        String oldStatus = complaint.getStatus().toString();
        System.out.println("Old Status: " + oldStatus);
        System.out
                .println("Complaint User: " + (complaint.getUser() != null ? complaint.getUser().getEmail() : "NULL"));
        System.out.println("Submission Type: " + complaint.getSubmissionType());

        complaint.setStatus(newStatus);
        if (newStatus == Complaint.Status.RESOLVED || newStatus == Complaint.Status.CLOSED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        ComplaintUpdate update = new ComplaintUpdate(complaint, newStatus, comment, updatedBy);
        complaint.getUpdates().add(update);

        complaint = complaintRepository.save(complaint);
        System.out.println("✅ Complaint saved to database");

        ComplaintResponse response = mapToResponse(complaint);

        // Send email notification about status change
        System.out.println("📧 Calling sendStatusChangeNotification...");
        notificationService.sendStatusChangeNotification(complaint, oldStatus,
                newStatus.toString(), updatedBy.getFullName());
        System.out.println("✅ sendStatusChangeNotification completed");

        // Send real-time notification for status change
        webSocketService.notifyStatusChange(response, oldStatus, newStatus.toString());

        System.out.println("=".repeat(80) + "\n");

        return response;
    }

    public ComplaintResponse getComplaintByComplaintId(String complaintId) {
        Complaint complaint = complaintRepository.findByComplaintIdWithAttachments(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return mapToResponse(complaint);
    }

    public Page<ComplaintResponse> getComplaints(Pageable pageable) {
        return complaintRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public Page<ComplaintResponse> getComplaintsByUser(Long userId, Pageable pageable) {
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    private String generateComplaintId() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        Long count = complaintRepository.count() + 1;
        return String.format("C%s%03d", year, count);
    }

    public ComplaintResponse mapToResponse(Complaint complaint) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setComplaintId(complaint.getComplaintId());
        response.setSubject(complaint.getSubject());
        response.setDescription(complaint.getDescription());
        response.setCategory(complaint.getCategory());
        response.setPriority(complaint.getPriority());
        response.setStatus(complaint.getStatus());
        response.setSubmissionType(complaint.getSubmissionType());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setUpdatedAt(complaint.getUpdatedAt());
        response.setResolvedAt(complaint.getResolvedAt());
        response.setEscalationEligibleAt(complaint.getEscalationEligibleAt());

        // Set anonymous flag for frontend handling
        if (complaint.getSubmissionType() == Complaint.SubmissionType.ANONYMOUS) {
            response.setAnonymous(true);
        }

        // Map updates
        List<ComplaintResponse.ComplaintUpdateResponse> updates = complaint.getUpdates().stream()
                .map(update -> {
                    ComplaintResponse.ComplaintUpdateResponse updateResponse = new ComplaintResponse.ComplaintUpdateResponse();
                    updateResponse.setId(update.getId());
                    updateResponse.setStatus(update.getStatus());
                    updateResponse.setComment(update.getComment());
                    updateResponse.setIsPublic(update.getIsPublic());
                    updateResponse.setUpdatedBy(
                            update.getUpdatedBy() != null ? update.getUpdatedBy().getFullName() : "System");
                    updateResponse.setCreatedAt(update.getCreatedAt());
                    return updateResponse;
                })
                .collect(Collectors.toList());
        response.setUpdates(updates);

        // Map attachments
        List<ComplaintResponse.AttachmentResponse> attachments = complaint.getAttachments().stream()
                .map(attachment -> {
                    ComplaintResponse.AttachmentResponse attachmentResponse = new ComplaintResponse.AttachmentResponse();
                    attachmentResponse.setId(attachment.getId());
                    attachmentResponse.setFileName(attachment.getFileName());
                    attachmentResponse.setFileSize(attachment.getFileSize());
                    attachmentResponse.setMimeType(attachment.getMimeType());
                    attachmentResponse.setUploadedAt(attachment.getUploadedAt());
                    return attachmentResponse;
                })
                .collect(Collectors.toList());
        response.setAttachments(attachments);

        // Map assigned user
        if (complaint.getAssignedTo() != null) {
            ComplaintResponse.UserResponse assignedToResponse = new ComplaintResponse.UserResponse(
                    complaint.getAssignedTo().getId(),
                    complaint.getAssignedTo().getFullName(),
                    complaint.getAssignedTo().getEmail(),
                    complaint.getAssignedTo().getRole().name());
            response.setAssignedTo(assignedToResponse);
        }

        return response;
    }

    public List<ComplaintAttachment> getComplaintAttachments(String complaintId) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return complaint.getAttachments();
    }

    public ComplaintAttachment getComplaintAttachment(String complaintId, Long attachmentId) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        return complaint.getAttachments().stream()
                .filter(attachment -> attachment.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
    }
}