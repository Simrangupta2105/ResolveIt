package com.resolve.complaint.repository;

import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
        Optional<Complaint> findByComplaintId(String complaintId);

        Page<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

        Page<Complaint> findByStatusOrderByCreatedAtDesc(Complaint.Status status, Pageable pageable);

        @Query("SELECT c FROM Complaint c WHERE c.submissionType = 'PUBLIC' OR c.user.id = :userId ORDER BY c.createdAt DESC")
        Page<Complaint> findPublicComplaintsOrByUser(@Param("userId") Long userId, Pageable pageable);

        Long countByStatus(Complaint.Status status);

        @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status IN ('NEW', 'UNDER_REVIEW', 'IN_PROGRESS')")
        Long countOpenComplaints();

        @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status IN ('RESOLVED', 'CLOSED')")
        Long countResolvedComplaints();

        // Additional methods for admin functionality
        List<Complaint> findByStatus(Complaint.Status status);

        List<Complaint> findByStatusAndResolvedAtIsNotNull(Complaint.Status status);

        List<Complaint> findByStatusIn(List<Complaint.Status> statuses);

        List<Complaint> findByAssignedTo(User assignedTo);

        List<Complaint> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

        List<Complaint> findByCreatedAtAfter(LocalDateTime date);

        long countByStatusIn(List<Complaint.Status> statuses);

        @Query("SELECT c FROM Complaint c WHERE " +
                        "(:status IS NULL OR c.status = :status) AND " +
                        "(:category IS NULL OR c.category = :category) AND " +
                        "(:priority IS NULL OR c.priority = :priority) AND " +
                        "(:assignedTo IS NULL OR c.assignedTo.id = :assignedTo) AND " +
                        "(:search IS NULL OR LOWER(c.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.complaintId) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<Complaint> findFilteredComplaints(
                        @Param("status") Complaint.Status status,
                        @Param("category") Complaint.Category category,
                        @Param("priority") Complaint.Priority priority,
                        @Param("assignedTo") Long assignedTo,
                        @Param("search") String search,
                        Pageable pageable);

        // Find complaints eligible for auto-escalation (older than cutoff date and not
        // escalated/resolved)
        @Query("SELECT c FROM Complaint c WHERE c.createdAt < :cutoffDate AND c.status NOT IN ('ESCALATED', 'RESOLVED', 'CLOSED') AND c.submissionType = 'PUBLIC'")
        List<Complaint> findComplaintsForAutoEscalation(@Param("cutoffDate") LocalDateTime cutoffDate);

        // Fetch all complaints with attachments eagerly loaded
        @Query("SELECT DISTINCT c FROM Complaint c LEFT JOIN FETCH c.attachments")
        List<Complaint> findAllWithAttachments();

        // Fetch a single complaint with attachments and updates eagerly loaded
        @Query("SELECT c FROM Complaint c LEFT JOIN FETCH c.attachments WHERE c.complaintId = :complaintId")
        Optional<Complaint> findByComplaintIdWithAttachments(@Param("complaintId") String complaintId);
}