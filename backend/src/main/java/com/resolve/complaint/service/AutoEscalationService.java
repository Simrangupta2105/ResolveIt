package com.resolve.complaint.service;

import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.model.User;
import com.resolve.complaint.repository.ComplaintRepository;
import com.resolve.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AutoEscalationService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    // Run every hour to check for complaints that need auto-escalation
    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 milliseconds
    public void checkForAutoEscalation() {
        System.out.println("🔄 AutoEscalationService: Checking for complaints to auto-escalate...");
        
        // Find complaints that are older than 7 days and not yet escalated or resolved
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        
        List<Complaint> complaintsToEscalate = complaintRepository.findComplaintsForAutoEscalation(sevenDaysAgo);
        
        System.out.println("📋 Found " + complaintsToEscalate.size() + " complaints eligible for auto-escalation");
        
        for (Complaint complaint : complaintsToEscalate) {
            try {
                autoEscalateComplaint(complaint);
            } catch (Exception e) {
                System.err.println("❌ Failed to auto-escalate complaint " + complaint.getComplaintId() + ": " + e.getMessage());
            }
        }
    }

    private void autoEscalateComplaint(Complaint complaint) {
        System.out.println("🚨 Auto-escalating complaint: " + complaint.getComplaintId());
        
        // Update complaint status to ESCALATED
        complaint.setStatus(Complaint.Status.ESCALATED);
        complaint.setUpdatedAt(LocalDateTime.now());
        
        // Find senior manager (Disha Shah)
        User seniorManager = userRepository.findByEmail("dishasah924@gmail.com")
            .orElse(null);
        
        if (seniorManager == null) {
            System.err.println("❌ Senior manager (Disha Shah) not found for auto-escalation");
            return;
        }
        
        // Assign to senior manager
        complaint.setAssignedTo(seniorManager);
        
        // Save the complaint
        complaintRepository.save(complaint);
        
        // Send email notifications
        sendAutoEscalationEmails(complaint, seniorManager);
        
        System.out.println("✅ Complaint " + complaint.getComplaintId() + " auto-escalated to " + seniorManager.getFullName());
    }

    private void sendAutoEscalationEmails(Complaint complaint, User seniorManager) {
        String escalationReason = "Automatic escalation after 7 days without resolution";
        
        // 1. Send email to the complainant (if it's a PUBLIC complaint)
        if (complaint.getUser() != null && complaint.getSubmissionType() == Complaint.SubmissionType.PUBLIC) {
            sendEscalationEmailToUser(complaint, escalationReason);
        }
        
        // 2. Send email to senior manager (Disha Shah)
        sendEscalationEmailToManager(complaint, seniorManager, escalationReason);
    }

    private void sendEscalationEmailToUser(Complaint complaint, String reason) {
        try {
            String subject = "Your Complaint Has Been Escalated - " + complaint.getComplaintId();
            String body = String.format(
                "Dear %s,\n\n" +
                "Your complaint has been automatically escalated to senior management for priority attention.\n\n" +
                "Complaint Details:\n" +
                "ID: %s\n" +
                "Subject: %s\n" +
                "Category: %s\n" +
                "Priority: %s\n" +
                "Submitted: %s\n" +
                "Reason for Escalation: %s\n\n" +
                "Your complaint is now being reviewed by our senior management team and will receive priority attention. " +
                "We apologize for any delay and appreciate your patience.\n\n" +
                "You can track your complaint status at: http://localhost:3000/complaint/%s\n\n" +
                "Thank you for bringing this matter to our attention.\n\n" +
                "Best regards,\n" +
                "Complaint Portal Team",
                complaint.getUser().getFullName() != null ? complaint.getUser().getFullName() : complaint.getUser().getUsername(),
                complaint.getComplaintId(),
                complaint.getSubject(),
                complaint.getCategory(),
                complaint.getPriority(),
                complaint.getCreatedAt().toLocalDate(),
                reason,
                complaint.getComplaintId()
            );
            
            emailService.sendEmail(complaint.getUser().getEmail(), subject, body);
            System.out.println("📧 Auto-escalation email sent to user: " + complaint.getUser().getEmail());
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send escalation email to user: " + e.getMessage());
        }
    }

    private void sendEscalationEmailToManager(Complaint complaint, User seniorManager, String reason) {
        try {
            String subject = "URGENT: Complaint Auto-Escalated - " + complaint.getComplaintId();
            String body = String.format(
                "Dear %s,\n\n" +
                "A complaint has been automatically escalated to your attention due to extended resolution time.\n\n" +
                "Complaint Details:\n" +
                "ID: %s\n" +
                "Subject: %s\n" +
                "Category: %s\n" +
                "Priority: %s\n" +
                "Submitted: %s\n" +
                "Current Status: ESCALATED\n" +
                "Reason: %s\n\n" +
                "Complainant: %s\n" +
                "Contact: %s\n\n" +
                "Description:\n" +
                "%s\n\n" +
                "This complaint requires immediate senior management attention. Please review and take appropriate action.\n\n" +
                "Access the complaint management system at: http://localhost:3000\n" +
                "Direct link: http://localhost:3000/complaint/%s\n\n" +
                "Best regards,\n" +
                "Complaint Portal Auto-Escalation System",
                seniorManager.getFullName(),
                complaint.getComplaintId(),
                complaint.getSubject(),
                complaint.getCategory(),
                complaint.getPriority(),
                complaint.getCreatedAt().toLocalDate(),
                reason,
                complaint.getSubmissionType() == Complaint.SubmissionType.ANONYMOUS ? 
                    "Anonymous User" : 
                    (complaint.getUser() != null ? complaint.getUser().getFullName() : "Unknown"),
                complaint.getSubmissionType() == Complaint.SubmissionType.ANONYMOUS ? 
                    "Anonymous" : 
                    (complaint.getUser() != null ? complaint.getUser().getEmail() : "Unknown"),
                complaint.getDescription(),
                complaint.getComplaintId()
            );
            
            emailService.sendEmail(seniorManager.getEmail(), subject, body);
            System.out.println("📧 Auto-escalation email sent to senior manager: " + seniorManager.getEmail());
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send escalation email to manager: " + e.getMessage());
        }
    }

    // Manual method to trigger auto-escalation check (for testing)
    public void triggerAutoEscalationCheck() {
        System.out.println("🔄 Manual trigger: Checking for auto-escalation...");
        checkForAutoEscalation();
    }
}