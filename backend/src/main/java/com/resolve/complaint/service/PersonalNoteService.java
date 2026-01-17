package com.resolve.complaint.service;

import com.resolve.complaint.model.PersonalNote;
import com.resolve.complaint.model.User;
import com.resolve.complaint.repository.PersonalNoteRepository;
import com.resolve.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalNoteService {

    @Autowired
    private PersonalNoteRepository personalNoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WebSocketService webSocketService;

    public PersonalNote sendPersonalNote(Long fromAdminId, Long toEmployeeId, String message) {
        User fromAdmin = userRepository.findById(fromAdminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        User toEmployee = userRepository.findById(toEmployeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Verify sender is admin
        if (!fromAdmin.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Only admins can send personal notes");
        }

        // Verify recipient is employee/manager/supervisor
        if (toEmployee.getRole().equals(User.Role.USER)) {
            throw new RuntimeException("Personal notes can only be sent to staff members");
        }

        PersonalNote note = new PersonalNote(message, fromAdmin, toEmployee);
        PersonalNote savedNote = personalNoteRepository.save(note);

        // Send real-time notification
        try {
            webSocketService.sendPersonalNoteNotification(toEmployee.getId(), 
                "New personal note from " + fromAdmin.getFullName(), savedNote);
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket notification: " + e.getMessage());
        }

        // Send email notification
        try {
            notificationService.sendPersonalNoteEmail(toEmployee, fromAdmin, message);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return savedNote;
    }

    public Page<PersonalNote> getNotesForEmployee(Long employeeId, int page, int size) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        return personalNoteRepository.findByToEmployeeOrderByCreatedAtDesc(employee, pageable);
    }

    public Page<PersonalNote> getNotesSentByAdmin(Long adminId, int page, int size) {
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        return personalNoteRepository.findByFromAdminOrderByCreatedAtDesc(admin, pageable);
    }

    public List<PersonalNote> getUnreadNotesForEmployee(Long employeeId) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        return personalNoteRepository.findByToEmployeeAndIsReadFalseOrderByCreatedAtDesc(employee);
    }

    public long getUnreadCountForEmployee(Long employeeId) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        return personalNoteRepository.countByToEmployeeAndIsReadFalse(employee);
    }

    public PersonalNote markAsRead(Long noteId, Long employeeId) {
        PersonalNote note = personalNoteRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("Personal note not found"));

        // Verify the note belongs to the employee
        if (!note.getToEmployee().getId().equals(employeeId)) {
            throw new RuntimeException("Access denied: This note is not for you");
        }

        note.setIsRead(true);
        return personalNoteRepository.save(note);
    }

    public void deleteNote(Long noteId, Long adminId) {
        PersonalNote note = personalNoteRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("Personal note not found"));

        // Verify the note was sent by this admin
        if (!note.getFromAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Access denied: You can only delete notes you sent");
        }

        personalNoteRepository.delete(note);
    }
}