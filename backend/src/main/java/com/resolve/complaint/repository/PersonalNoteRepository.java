package com.resolve.complaint.repository;

import com.resolve.complaint.model.PersonalNote;
import com.resolve.complaint.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalNoteRepository extends JpaRepository<PersonalNote, Long> {
    
    // Find notes sent to a specific employee
    Page<PersonalNote> findByToEmployeeOrderByCreatedAtDesc(User toEmployee, Pageable pageable);
    
    // Find notes sent by a specific admin
    Page<PersonalNote> findByFromAdminOrderByCreatedAtDesc(User fromAdmin, Pageable pageable);
    
    // Find unread notes for a specific employee
    List<PersonalNote> findByToEmployeeAndIsReadFalseOrderByCreatedAtDesc(User toEmployee);
    
    // Count unread notes for a specific employee
    long countByToEmployeeAndIsReadFalse(User toEmployee);
    
    // Find notes between specific admin and employee
    @Query("SELECT pn FROM PersonalNote pn WHERE pn.fromAdmin = :admin AND pn.toEmployee = :employee ORDER BY pn.createdAt DESC")
    Page<PersonalNote> findNotesBetweenAdminAndEmployee(@Param("admin") User admin, @Param("employee") User employee, Pageable pageable);
}