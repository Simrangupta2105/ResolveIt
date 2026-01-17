package com.resolve.complaint.repository;

import com.resolve.complaint.model.ComplaintUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintUpdateRepository extends JpaRepository<ComplaintUpdate, Long> {
    List<ComplaintUpdate> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
    List<ComplaintUpdate> findByComplaintIdAndIsPublicTrueOrderByCreatedAtDesc(Long complaintId);
}