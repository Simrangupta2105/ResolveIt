package com.resolve.complaint.controller;

import com.resolve.complaint.model.PersonalNote;
import com.resolve.complaint.security.UserPrincipal;
import com.resolve.complaint.service.PersonalNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/personal-notes")
public class PersonalNoteController {

    @Autowired
    private PersonalNoteService personalNoteService;

    // Admin sends personal note to employee
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendPersonalNote(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long toEmployeeId = Long.valueOf(request.get("toEmployeeId").toString());
        String message = (String) request.get("message");
        
        try {
            PersonalNote note = personalNoteService.sendPersonalNote(
                userPrincipal.getId(), toEmployeeId, message);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Personal note sent successfully");
            response.put("noteId", note.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Employee gets their personal notes
    @GetMapping("/my-notes")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'SUPERVISOR')")
    public ResponseEntity<Page<PersonalNote>> getMyPersonalNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Page<PersonalNote> notes = personalNoteService.getNotesForEmployee(
            userPrincipal.getId(), page, size);
        
        return ResponseEntity.ok(notes);
    }

    // Employee gets unread personal notes
    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'SUPERVISOR')")
    public ResponseEntity<List<PersonalNote>> getUnreadNotes(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<PersonalNote> unreadNotes = personalNoteService.getUnreadNotesForEmployee(
            userPrincipal.getId());
        
        return ResponseEntity.ok(unreadNotes);
    }

    // Employee gets unread count
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        long count = personalNoteService.getUnreadCountForEmployee(userPrincipal.getId());
        
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }

    // Employee marks note as read
    @PutMapping("/{noteId}/read")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'SUPERVISOR')")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long noteId,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        try {
            PersonalNote note = personalNoteService.markAsRead(noteId, userPrincipal.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Note marked as read");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Admin gets notes they sent
    @GetMapping("/sent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PersonalNote>> getSentNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Page<PersonalNote> notes = personalNoteService.getNotesSentByAdmin(
            userPrincipal.getId(), page, size);
        
        return ResponseEntity.ok(notes);
    }

    // Admin deletes a note they sent
    @DeleteMapping("/{noteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteNote(
            @PathVariable Long noteId,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        try {
            personalNoteService.deleteNote(noteId, userPrincipal.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Note deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}