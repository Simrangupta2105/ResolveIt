package com.resolve.complaint.dto;

import com.resolve.complaint.model.Complaint;
import java.time.LocalDateTime;
import java.util.List;

public class ComplaintResponse {
    private Long id;
    private String complaintId;
    private String subject;
    private String description;
    private Complaint.Category category;
    private Complaint.Priority priority;
    private Complaint.Status status;
    private Complaint.SubmissionType submissionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime escalationEligibleAt;
    private List<ComplaintUpdateResponse> updates;
    private List<AttachmentResponse> attachments;
    private boolean isAnonymous;
    private UserResponse assignedTo;

    // Constructors
    public ComplaintResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComplaintId() { 
        // Hide complaint ID for anonymous complaints in public view
        if (isAnonymous && submissionType == Complaint.SubmissionType.ANONYMOUS) {
            return "ANONYMOUS-****";
        }
        return complaintId; 
    }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    // Method to get the actual complaint ID (for admin/internal use)
    public String getActualComplaintId() { return complaintId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Complaint.Category getCategory() { return category; }
    public void setCategory(Complaint.Category category) { this.category = category; }

    public Complaint.Priority getPriority() { return priority; }
    public void setPriority(Complaint.Priority priority) { this.priority = priority; }

    public Complaint.Status getStatus() { return status; }
    public void setStatus(Complaint.Status status) { this.status = status; }

    public Complaint.SubmissionType getSubmissionType() { return submissionType; }
    public void setSubmissionType(Complaint.SubmissionType submissionType) { this.submissionType = submissionType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getEscalationEligibleAt() { return escalationEligibleAt; }
    public void setEscalationEligibleAt(LocalDateTime escalationEligibleAt) { this.escalationEligibleAt = escalationEligibleAt; }

    public List<ComplaintUpdateResponse> getUpdates() { return updates; }
    public void setUpdates(List<ComplaintUpdateResponse> updates) { this.updates = updates; }

    public List<AttachmentResponse> getAttachments() { return attachments; }
    public void setAttachments(List<AttachmentResponse> attachments) { this.attachments = attachments; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public UserResponse getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UserResponse assignedTo) { this.assignedTo = assignedTo; }

    public static class ComplaintUpdateResponse {
        private Long id;
        private Complaint.Status status;
        private String comment;
        private Boolean isPublic;
        private String updatedBy;
        private LocalDateTime createdAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Complaint.Status getStatus() { return status; }
        public void setStatus(Complaint.Status status) { this.status = status; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public Boolean getIsPublic() { return isPublic; }
        public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class AttachmentResponse {
        private Long id;
        private String fileName;
        private Long fileSize;
        private String mimeType;
        private LocalDateTime uploadedAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }

        public LocalDateTime getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    }

    public static class UserResponse {
        private Long id;
        private String fullName;
        private String email;
        private String role;

        // Constructors
        public UserResponse() {}

        public UserResponse(Long id, String fullName, String email, String role) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}