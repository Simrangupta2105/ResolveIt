package com.resolve.complaint.service;

import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private EmailService emailService;

    public void sendAssignmentNotification(Complaint complaint, User assignedTo) {
        System.out.println(String.format(
            "NOTIFICATION: Complaint %s has been assigned to %s",
            complaint.getComplaintId(),
            assignedTo.getFullName()
        ));
        
        // Send email notification
        emailService.sendAssignmentEmail(assignedTo, complaint, "System Admin");
    }

    public void sendEscalationNotification(Complaint complaint, String reason, User higherAuthority) {
        System.out.println(String.format(
            "NOTIFICATION: Complaint %s has been escalated. Reason: %s",
            complaint.getComplaintId(),
            reason
        ));
        
        if (higherAuthority != null) {
            System.out.println(String.format(
                "NOTIFICATION: Escalated complaint %s assigned to %s",
                complaint.getComplaintId(),
                higherAuthority.getFullName()
            ));
        }
        
        // Notify original complainant if not anonymous
        if (complaint.getUser() != null && complaint.getSubmissionType() == Complaint.SubmissionType.PUBLIC) {
            System.out.println(String.format(
                "NOTIFICATION: Sending escalation update to complainant for %s",
                complaint.getComplaintId()
            ));
            emailService.sendEscalationEmail(complaint.getUser(), complaint, reason, "System Admin");
        }
    }

    public void sendUpdateNotification(Complaint complaint, String update) {
        System.out.println(String.format(
            "NOTIFICATION: Update for complaint %s: %s",
            complaint.getComplaintId(),
            update
        ));
        
        // Only send to complainant if it's a public complaint
        if (complaint.getUser() != null && complaint.getSubmissionType() == Complaint.SubmissionType.PUBLIC) {
            System.out.println(String.format(
                "NOTIFICATION: Sending update to %s for complaint %s",
                complaint.getUser().getEmail(),
                complaint.getComplaintId()
            ));
        }
    }

    public void sendStatusChangeNotification(Complaint complaint, String oldStatus, String newStatus, String updatedBy) {
        System.out.println(String.format(
            "NOTIFICATION: Complaint %s status changed from %s to %s by %s",
            complaint.getComplaintId(),
            oldStatus,
            newStatus,
            updatedBy
        ));
        
        System.out.println("DEBUG: Complaint user: " + (complaint.getUser() != null ? complaint.getUser().getEmail() : "NULL"));
        System.out.println("DEBUG: Submission type: " + complaint.getSubmissionType());
        
        // Send email notification to complainant if it's a public complaint
        if (complaint.getUser() != null && complaint.getSubmissionType() == Complaint.SubmissionType.PUBLIC) {
            System.out.println("✅ Sending status update email to: " + complaint.getUser().getEmail());
            emailService.sendStatusUpdateEmail(complaint.getUser(), complaint, oldStatus, newStatus, updatedBy);
        } else {
            System.out.println("⚠️ Email NOT sent - User is null or complaint is anonymous");
        }
    }

    public void sendResolutionNotification(Complaint complaint) {
        System.out.println(String.format(
            "NOTIFICATION: Complaint %s has been resolved",
            complaint.getComplaintId()
        ));
        
        // Send resolution email if it's a public complaint
        if (complaint.getUser() != null && complaint.getSubmissionType() == Complaint.SubmissionType.PUBLIC) {
            emailService.sendStatusUpdateEmail(complaint.getUser(), complaint, 
                complaint.getStatus().toString(), "RESOLVED", "System");
        }
    }

    public void sendPersonalNoteEmail(User toEmployee, User fromAdmin, String message) {
        try {
            String subject = "Personal Note from " + fromAdmin.getFullName();
            
            String body = String.format(
                "Dear %s,\n\n" +
                "You have received a personal note from %s:\n\n" +
                "%s\n\n" +
                "Please log in to your dashboard to view and respond.\n\n" +
                "Best regards,\n" +
                "Complaint Portal System",
                toEmployee.getFullName() != null ? toEmployee.getFullName() : toEmployee.getUsername(),
                fromAdmin.getFullName() != null ? fromAdmin.getFullName() : fromAdmin.getUsername(),
                message
            );
            
            emailService.sendEmail(toEmployee.getEmail(), subject, body);
            System.out.println("Personal note email sent to: " + toEmployee.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send personal note email: " + e.getMessage());
        }
    }
}