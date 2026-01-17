package com.resolve.complaint.repository;

import com.resolve.complaint.model.EmployeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRequestRepository extends JpaRepository<EmployeeRequest, Long> {
    Page<EmployeeRequest> findByStatusOrderByRequestedAtDesc(EmployeeRequest.Status status, Pageable pageable);
    Long countByStatus(EmployeeRequest.Status status);
}