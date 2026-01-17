package com.resolve.complaint.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal_notes")
public class PersonalNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_admin_id", nullable = false)
    private User fromAdmin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_employee_id", nullable = false)
    private User toEmployee;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public PersonalNote() {}

    public PersonalNote(String message, User fromAdmin, User toEmployee) {
        this.message = message;
        this.fromAdmin = fromAdmin;
        this.toEmployee = toEmployee;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public User getFromAdmin() { return fromAdmin; }
    public void setFromAdmin(User fromAdmin) { this.fromAdmin = fromAdmin; }

    public User getToEmployee() { return toEmployee; }
    public void setToEmployee(User toEmployee) { this.toEmployee = toEmployee; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { 
        this.isRead = isRead;
        if (isRead && readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}