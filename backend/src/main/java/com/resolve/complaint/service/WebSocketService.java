package com.resolve.complaint.service;

import com.resolve.complaint.dto.ComplaintResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyNewComplaint(ComplaintResponse complaint) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_COMPLAINT");
        notification.put("complaint", complaint);
        notification.put("message", "New complaint submitted: " + complaint.getSubject());
        notification.put("timestamp", System.currentTimeMillis());

        // Send to all connected clients
        messagingTemplate.convertAndSend("/topic/complaints", notification);
    }

    public void notifyComplaintUpdate(ComplaintResponse complaint, String updateType) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "COMPLAINT_UPDATE");
        notification.put("updateType", updateType);
        notification.put("complaint", complaint);
        notification.put("message", "Complaint " + complaint.getComplaintId() + " has been updated");
        notification.put("timestamp", System.currentTimeMillis());

        // Send to all connected clients
        messagingTemplate.convertAndSend("/topic/complaints", notification);
    }

    public void notifyStatusChange(ComplaintResponse complaint, String oldStatus, String newStatus) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "STATUS_CHANGE");
        notification.put("complaint", complaint);
        notification.put("oldStatus", oldStatus);
        notification.put("newStatus", newStatus);
        notification.put("message", "Complaint " + complaint.getComplaintId() + " status changed from " + oldStatus + " to " + newStatus);
        notification.put("timestamp", System.currentTimeMillis());

        // Send to all connected clients
        messagingTemplate.convertAndSend("/topic/complaints", notification);
    }

    public void notifyAssignment(ComplaintResponse complaint, String assignedTo) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ASSIGNMENT");
        notification.put("complaint", complaint);
        notification.put("assignedTo", assignedTo);
        notification.put("message", "Complaint " + complaint.getComplaintId() + " assigned to " + assignedTo);
        notification.put("timestamp", System.currentTimeMillis());

        // Send to all connected clients
        messagingTemplate.convertAndSend("/topic/complaints", notification);
    }

    public void notifyEscalation(ComplaintResponse complaint, String reason) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ESCALATION");
        notification.put("complaint", complaint);
        notification.put("reason", reason);
        notification.put("message", "Complaint " + complaint.getComplaintId() + " has been escalated");
        notification.put("timestamp", System.currentTimeMillis());

        // Send to all connected clients
        messagingTemplate.convertAndSend("/topic/complaints", notification);
    }

    public void sendPersonalNoteNotification(Long employeeId, String message, Object noteData) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "PERSONAL_NOTE");
            notification.put("message", message);
            notification.put("timestamp", LocalDateTime.now().toString());
            notification.put("data", noteData);
            
            // Send to specific employee
            messagingTemplate.convertAndSendToUser(
                employeeId.toString(), 
                "/queue/personal-notes", 
                notification
            );
            
            System.out.println("Personal note notification sent to employee: " + employeeId);
        } catch (Exception e) {
            System.err.println("Failed to send personal note notification: " + e.getMessage());
        }
    }
}