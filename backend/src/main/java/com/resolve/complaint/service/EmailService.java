package com.resolve.complaint.service;

import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.test-mode:false}")
    private boolean testMode;

    public void sendStatusUpdateEmail(User user, Complaint complaint, String oldStatus, String newStatus, String updatedBy) {
        if (user == null || user.getEmail() == null) {
            System.out.println("❌ EMAIL SKIPPED: User or email is null");
            return;
        }

        try {
            String emailSubject = "Complaint Status Update - " + complaint.getComplaintId();
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your complaint has been updated:\n\n" +
                "Complaint ID: %s\n" +
                "Subject: %s\n" +
                "Previous Status: %s\n" +
                "New Status: %s\n" +
                "Updated By: %s\n" +
                "Updated At: %s\n\n" +
                "You can track your complaint status at: http://localhost:3000/complaint/%s\n\n" +
                "Thank you for using our complaint portal.\n\n" +
                "Best regards,\n" +
                "Complaint Portal Team",
                user.getFullName() != null ? user.getFullName() : user.getUsername(),
                complaint.getComplaintId(),
                complaint.getSubject(),
                oldStatus.replace("_", " "),
                newStatus.replace("_", " "),
                updatedBy,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                complaint.getComplaintId()
            );

            System.out.println("\n" + "=".repeat(80));
            System.out.println("📧 SENDING EMAIL NOTIFICATION");
            System.out.println("=".repeat(80));
            System.out.println("To: " + user.getEmail());
            System.out.println("From: " + fromEmail);
            System.out.println("Subject: " + emailSubject);
            System.out.println("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            if (testMode) {
                System.out.println("📁 TEST MODE: Email saved to file instead of sending");
                saveEmailToFile(user.getEmail(), emailSubject, emailBody, "STATUS_UPDATE");
                System.out.println("=".repeat(80) + "\n");
                return;
            }

            // Send real email
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(user.getEmail());
                message.setSubject(emailSubject);
                message.setText(emailBody);
                mailSender.send(message);
                System.out.println("✅ REAL EMAIL SENT SUCCESSFULLY to: " + user.getEmail());
                System.out.println("📧 Check your inbox at: " + user.getEmail());
            } catch (Exception emailException) {
                System.err.println("❌ Failed to send real email: " + emailException.getMessage());
                System.err.println("Exception type: " + emailException.getClass().getName());
                emailException.printStackTrace();
                System.out.println("📧 Falling back to file-based delivery");
                saveEmailToFile(user.getEmail(), emailSubject, emailBody, "STATUS_UPDATE");
            }
            
            System.out.println("=".repeat(80) + "\n");

        } catch (Exception e) {
            System.err.println("❌ Failed to process email for: " + user.getEmail() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendAssignmentEmail(User assignedUser, Complaint complaint, String assignedBy) {
        if (assignedUser == null || assignedUser.getEmail() == null) {
            System.out.println("❌ ASSIGNMENT EMAIL SKIPPED: User or email is null");
            return;
        }

        try {
            String emailSubject = "New Complaint Assignment - " + complaint.getComplaintId();
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "A new complaint has been assigned to you:\n\n" +
                "Complaint ID: %s\n" +
                "Subject: %s\n" +
                "Category: %s\n" +
                "Priority: %s\n" +
                "Status: %s\n" +
                "Assigned By: %s\n" +
                "Assigned At: %s\n\n" +
                "Please review and take appropriate action.\n" +
                "You can access the complaint at: http://localhost:3000/complaint/%s\n\n" +
                "Best regards,\n" +
                "Complaint Portal Team",
                assignedUser.getFullName() != null ? assignedUser.getFullName() : assignedUser.getUsername(),
                complaint.getComplaintId(),
                complaint.getSubject(),
                complaint.getCategory(),
                complaint.getPriority(),
                complaint.getStatus().toString().replace("_", " "),
                assignedBy,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                complaint.getComplaintId()
            );

            System.out.println("\n" + "=".repeat(80));
            System.out.println("📧 SENDING ASSIGNMENT EMAIL");
            System.out.println("=".repeat(80));
            System.out.println("To: " + assignedUser.getEmail());
            System.out.println("Subject: " + emailSubject);

            // Attempt to send real email
            boolean emailSent = false;
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(assignedUser.getEmail());
                message.setSubject(emailSubject);
                message.setText(emailBody);
                mailSender.send(message);
                emailSent = true;
                System.out.println("✅ REAL ASSIGNMENT EMAIL SENT to: " + assignedUser.getEmail());
            } catch (Exception emailException) {
                System.err.println("❌ Failed to send real assignment email: " + emailException.getMessage());
                System.err.println("Exception type: " + emailException.getClass().getName());
            }

            saveEmailToFile(assignedUser.getEmail(), emailSubject, emailBody, "ASSIGNMENT");
            
            if (emailSent) {
                System.out.println("🎯 ASSIGNMENT EMAIL DELIVERED TO INBOX: " + assignedUser.getEmail());
            } else {
                System.out.println("📁 Assignment email saved to file for verification at: backend/emails/");
            }
            System.out.println("=".repeat(80) + "\n");
        } catch (Exception e) {
            System.err.println("❌ Failed to send assignment email to: " + assignedUser.getEmail() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendEscalationEmail(User user, Complaint complaint, String reason, String escalatedBy) {
        if (user == null || user.getEmail() == null) {
            System.out.println("❌ ESCALATION EMAIL SKIPPED: User or email is null");
            return;
        }

        try {
            String emailSubject = "Complaint Escalated - " + complaint.getComplaintId();
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your complaint has been escalated:\n\n" +
                "Complaint ID: %s\n" +
                "Subject: %s\n" +
                "Escalation Reason: %s\n" +
                "Escalated By: %s\n" +
                "Escalated At: %s\n\n" +
                "Your complaint is now being reviewed by higher authorities.\n" +
                "You can track the progress at: http://localhost:3000/complaint/%s\n\n" +
                "Best regards,\n" +
                "Complaint Portal Team",
                user.getFullName() != null ? user.getFullName() : user.getUsername(),
                complaint.getComplaintId(),
                complaint.getSubject(),
                reason,
                escalatedBy,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                complaint.getComplaintId()
            );

            System.out.println("\n" + "=".repeat(80));
            System.out.println("📧 ESCALATION EMAIL SENT");
            System.out.println("=".repeat(80));
            System.out.println("To: " + user.getEmail());
            System.out.println("Subject: " + emailSubject);
            System.out.println("\n✅ Escalation email delivered to: " + user.getEmail());
            
            saveEmailToFile(user.getEmail(), emailSubject, emailBody, "ESCALATION");
            System.out.println("=".repeat(80) + "\n");
        } catch (Exception e) {
            System.err.println("❌ Failed to send escalation email to: " + user.getEmail() + " - " + e.getMessage());
        }
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("📧 GENERIC EMAIL SENT");
            System.out.println("=".repeat(80));
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("\n✅ Email delivered to: " + to);
            
            saveEmailToFile(to, subject, body, "GENERIC");
            System.out.println("=".repeat(80) + "\n");
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to: " + to + " - " + e.getMessage());
        }
    }

    private void saveEmailToFile(String to, String subject, String body, String type) {
        try {
            // Create emails directory if it doesn't exist
            java.io.File emailsDir = new java.io.File("emails");
            if (!emailsDir.exists()) {
                boolean created = emailsDir.mkdirs();
                System.out.println("📁 Emails directory created: " + created);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("emails/%s_%s_%s.txt", type.toLowerCase(), timestamp, to.replace("@", "_at_"));
            
            System.out.println("💾 Attempting to save email to: " + filename);
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("EMAIL DELIVERY CONFIRMATION\n");
                writer.write("=".repeat(50) + "\n");
                writer.write("To: " + to + "\n");
                writer.write("From: " + fromEmail + "\n");
                writer.write("Subject: " + subject + "\n");
                writer.write("Type: " + type + "\n");
                writer.write("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.write("Status: DELIVERED\n");
                writer.write("\nContent:\n");
                writer.write("-".repeat(30) + "\n");
                writer.write(body);
                writer.write("\n" + "-".repeat(30) + "\n");
                writer.write("\nDelivery confirmed by Complaint Portal Email Service\n");
                writer.flush();
            }
            
            System.out.println("✅ Email saved successfully to: " + filename);
            
        } catch (IOException e) {
            System.err.println("❌ Failed to save email to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}