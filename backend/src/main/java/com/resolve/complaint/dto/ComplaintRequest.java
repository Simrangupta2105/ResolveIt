package com.resolve.complaint.dto;

import com.resolve.complaint.model.Complaint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ComplaintRequest {
    @NotBlank
    @Size(max = 200)
    private String subject;

    @NotBlank
    private String description;

    @NotNull
    private Complaint.Category category;

    private Complaint.Priority priority = Complaint.Priority.MEDIUM;

    @NotNull
    private Complaint.SubmissionType submissionType;

    // Constructors
    public ComplaintRequest() {}

    // Getters and Setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Complaint.Category getCategory() { return category; }
    public void setCategory(Complaint.Category category) { this.category = category; }

    public Complaint.Priority getPriority() { return priority; }
    public void setPriority(Complaint.Priority priority) { this.priority = priority; }

    public Complaint.SubmissionType getSubmissionType() { return submissionType; }
    public void setSubmissionType(Complaint.SubmissionType submissionType) { this.submissionType = submissionType; }
}