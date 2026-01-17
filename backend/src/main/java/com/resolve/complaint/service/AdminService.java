package com.resolve.complaint.service;

import com.resolve.complaint.dto.ComplaintResponse;
import com.resolve.complaint.dto.DashboardStatsResponse;
import com.resolve.complaint.model.*;
import com.resolve.complaint.repository.ComplaintRepository;
import com.resolve.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

        @Autowired
        private ComplaintRepository complaintRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ComplaintService complaintService;

        @Autowired
        private NotificationService notificationService;

        @Autowired
        private WebSocketService webSocketService;

        public DashboardStatsResponse getDashboardStats() {
                long totalComplaints = complaintRepository.count();
                long openComplaints = complaintRepository.countByStatusIn(
                                Arrays.asList(Complaint.Status.NEW, Complaint.Status.UNDER_REVIEW,
                                                Complaint.Status.IN_PROGRESS,
                                                Complaint.Status.ESCALATED));
                long resolvedComplaints = complaintRepository.countByStatus(Complaint.Status.RESOLVED);

                // Calculate average resolution time (include both RESOLVED and CLOSED
                // complaints)
                List<Complaint> resolvedComplaintsList = complaintRepository.findByStatusIn(
                                Arrays.asList(Complaint.Status.RESOLVED, Complaint.Status.CLOSED))
                                .stream()
                                .filter(complaint -> complaint.getResolvedAt() != null)
                                .collect(Collectors.toList());

                double averageResolutionTime = resolvedComplaintsList.stream()
                                .mapToLong(complaint -> ChronoUnit.DAYS.between(
                                                complaint.getCreatedAt(), complaint.getResolvedAt()))
                                .average()
                                .orElse(0.0);

                DashboardStatsResponse stats = new DashboardStatsResponse();
                stats.setTotalComplaints(totalComplaints);
                stats.setOpenComplaints(openComplaints);
                stats.setResolvedComplaints(resolvedComplaints);
                stats.setAverageResolutionTime(Math.round(averageResolutionTime));

                return stats;
        }

        public Page<ComplaintResponse> getFilteredComplaints(
                        Pageable pageable, String status, String category, String priority,
                        String assignedTo, String search) {

                // Fetch all complaints with attachments eagerly loaded
                List<Complaint> allComplaints = complaintRepository.findAllWithAttachments();

                // Apply filters
                List<Complaint> filteredComplaints = allComplaints.stream()
                                .filter(complaint -> {
                                        if (status != null && !status.isEmpty()) {
                                                return complaint.getStatus().toString().equals(status);
                                        }
                                        return true;
                                })
                                .filter(complaint -> {
                                        if (category != null && !category.isEmpty()) {
                                                return complaint.getCategory().toString().equals(category);
                                        }
                                        return true;
                                })
                                .filter(complaint -> {
                                        if (priority != null && !priority.isEmpty()) {
                                                return complaint.getPriority().toString().equals(priority);
                                        }
                                        return true;
                                })
                                .filter(complaint -> {
                                        if (assignedTo != null && !assignedTo.isEmpty()) {
                                                if ("unassigned".equals(assignedTo)) {
                                                        return complaint.getAssignedTo() == null;
                                                } else {
                                                        return complaint.getAssignedTo() != null &&
                                                                        complaint.getAssignedTo().getId().toString()
                                                                                        .equals(assignedTo);
                                                }
                                        }
                                        return true;
                                })
                                .filter(complaint -> {
                                        if (search != null && !search.isEmpty()) {
                                                String searchLower = search.toLowerCase();
                                                return complaint.getSubject().toLowerCase().contains(searchLower) ||
                                                                complaint.getDescription().toLowerCase()
                                                                                .contains(searchLower)
                                                                ||
                                                                complaint.getComplaintId().toLowerCase()
                                                                                .contains(searchLower);
                                        }
                                        return true;
                                })
                                .collect(Collectors.toList());

                // Apply pagination
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), filteredComplaints.size());
                List<Complaint> pageContent = filteredComplaints.subList(start, end);

                // Convert to response DTOs
                List<ComplaintResponse> responseList = pageContent.stream()
                                .map(complaint -> complaintService.mapToResponse(complaint))
                                .collect(Collectors.toList());

                return new PageImpl<>(responseList, pageable, filteredComplaints.size());
        }

        public ComplaintResponse assignComplaint(String complaintId, Long userId, Long assignedById) {
                Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                                .orElseThrow(() -> new RuntimeException("Complaint not found"));

                User assignedBy = userRepository.findById(assignedById)
                                .orElseThrow(() -> new RuntimeException("Assigning user not found"));

                User assignedTo = null;
                if (userId != null) {
                        assignedTo = userRepository.findById(userId)
                                        .orElseThrow(() -> new RuntimeException("Assigned user not found"));
                }

                complaint.setAssignedTo(assignedTo);

                // Add update record
                String comment = assignedTo != null
                                ? String.format("Complaint assigned to %s", assignedTo.getFullName())
                                : "Complaint unassigned";

                ComplaintUpdate update = new ComplaintUpdate(
                                complaint, complaint.getStatus(), comment, assignedBy);
                complaint.getUpdates().add(update);

                complaint = complaintRepository.save(complaint);

                ComplaintResponse response = complaintService.mapToResponse(complaint);

                // Send notification
                if (assignedTo != null) {
                        notificationService.sendAssignmentNotification(complaint, assignedTo);
                        // Send real-time notification for assignment
                        webSocketService.notifyAssignment(response, assignedTo.getFullName());
                }

                return response;
        }

        public ComplaintResponse escalateComplaint(String complaintId, Long higherAuthorityId,
                        String reason, boolean notifyAllParties, Long escalatedById) {
                Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                                .orElseThrow(() -> new RuntimeException("Complaint not found"));

                // Check if escalation is eligible (7-day rule)
                LocalDateTime now = LocalDateTime.now();
                if (complaint.getEscalationEligibleAt() != null && now.isBefore(complaint.getEscalationEligibleAt())) {
                        long daysRemaining = ChronoUnit.DAYS.between(now, complaint.getEscalationEligibleAt());
                        throw new RuntimeException(String.format(
                                        "Escalation not allowed yet. Please wait %d more day(s). Escalation will be available on %s",
                                        daysRemaining,
                                        complaint.getEscalationEligibleAt()
                                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                }

                User escalatedBy = userRepository.findById(escalatedById)
                                .orElseThrow(() -> new RuntimeException("Escalating user not found"));

                User higherAuthority = null;
                if (higherAuthorityId != null) {
                        higherAuthority = userRepository.findById(higherAuthorityId)
                                        .orElseThrow(() -> new RuntimeException("Higher authority not found"));
                }

                // Update complaint
                complaint.setAssignedTo(higherAuthority);
                complaint.setStatus(Complaint.Status.ESCALATED);

                // Add escalation record
                String comment = String.format("Complaint escalated. Reason: %s", reason);
                if (higherAuthority != null) {
                        comment += String.format(" Escalated to: %s", higherAuthority.getFullName());
                }

                ComplaintUpdate update = new ComplaintUpdate(
                                complaint, Complaint.Status.ESCALATED, comment, escalatedBy);
                update.setIsPublic(false); // Escalation notes are internal
                complaint.getUpdates().add(update);

                complaint = complaintRepository.save(complaint);

                ComplaintResponse response = complaintService.mapToResponse(complaint);

                // Send notifications
                if (notifyAllParties) {
                        notificationService.sendEscalationNotification(complaint, reason, higherAuthority);
                }

                // Send real-time notification for escalation
                webSocketService.notifyEscalation(response, reason);

                return response;
        }

        public ComplaintResponse addComplaintNote(String complaintId, String note,
                        boolean isPublic, Long addedById) {
                Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                                .orElseThrow(() -> new RuntimeException("Complaint not found"));

                User addedBy = userRepository.findById(addedById)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                ComplaintUpdate update = new ComplaintUpdate(
                                complaint, complaint.getStatus(), note, addedBy);
                update.setIsPublic(isPublic);
                complaint.getUpdates().add(update);

                complaint = complaintRepository.save(complaint);

                // Send notification if public note
                if (isPublic && complaint.getUser() != null) {
                        notificationService.sendUpdateNotification(complaint, note);
                }

                return complaintService.mapToResponse(complaint);
        }

        public ComplaintResponse addPrivateNote(String complaintId, String note, Long addedById) {
                Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                                .orElseThrow(() -> new RuntimeException("Complaint not found"));

                User addedBy = userRepository.findById(addedById)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                ComplaintUpdate update = new ComplaintUpdate(
                                complaint, complaint.getStatus(), note, addedBy);
                update.setIsPublic(false);
                update.setIsPrivateNote(true);
                complaint.getUpdates().add(update);

                complaint = complaintRepository.save(complaint);

                return complaintService.mapToResponse(complaint);
        }

        public Map<String, Object> generateComplaintReport(String startDate, String endDate,
                        String category, String status, String format) {
                LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + "T00:00:00")
                                : LocalDateTime.now().minusDays(30);
                LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : LocalDateTime.now();

                List<Complaint> complaints = complaintRepository.findByCreatedAtBetween(start, end);

                // Apply additional filters
                if (category != null && !category.isEmpty()) {
                        complaints = complaints.stream()
                                        .filter(c -> c.getCategory().toString().equals(category))
                                        .collect(Collectors.toList());
                }

                if (status != null && !status.isEmpty()) {
                        complaints = complaints.stream()
                                        .filter(c -> c.getStatus().toString().equals(status))
                                        .collect(Collectors.toList());
                }

                Map<String, Object> report = new HashMap<>();
                report.put("totalComplaints", complaints.size());
                report.put("dateRange", Map.of("start", start, "end", end));

                // Category breakdown
                Map<String, Long> categoryBreakdown = complaints.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getCategory().toString(),
                                                Collectors.counting()));
                report.put("categoryBreakdown", categoryBreakdown);

                // Status breakdown
                Map<String, Long> statusBreakdown = complaints.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getStatus().toString(),
                                                Collectors.counting()));
                report.put("statusBreakdown", statusBreakdown);

                // Priority breakdown
                Map<String, Long> priorityBreakdown = complaints.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getPriority().toString(),
                                                Collectors.counting()));
                report.put("priorityBreakdown", priorityBreakdown);

                // Submission type breakdown
                Map<String, Long> submissionTypeBreakdown = complaints.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getSubmissionType().toString(),
                                                Collectors.counting()));
                report.put("submissionTypeBreakdown", submissionTypeBreakdown);

                // Resolution metrics
                List<Complaint> resolvedComplaints = complaints.stream()
                                .filter(c -> c.getStatus() == Complaint.Status.RESOLVED)
                                .collect(Collectors.toList());

                double avgResolutionTime = resolvedComplaints.stream()
                                .mapToLong(c -> ChronoUnit.DAYS.between(c.getCreatedAt(), c.getResolvedAt()))
                                .average()
                                .orElse(0.0);

                report.put("resolutionMetrics", Map.of(
                                "resolvedCount", resolvedComplaints.size(),
                                "averageResolutionTime", Math.round(avgResolutionTime),
                                "resolutionRate",
                                complaints.size() > 0 ? (double) resolvedComplaints.size() / complaints.size() * 100
                                                : 0));

                return report;
        }

        public Map<String, Object> getComplaintTrends(int days) {
                LocalDateTime startDate = LocalDateTime.now().minusDays(days);
                List<Complaint> complaints = complaintRepository.findByCreatedAtAfter(startDate);

                Map<String, Object> trends = new HashMap<>();

                // Daily complaint counts
                Map<String, Long> dailyCounts = complaints.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getCreatedAt().toLocalDate().toString(),
                                                Collectors.counting()));
                trends.put("dailyCounts", dailyCounts);

                // Category trends
                Map<String, Long> categoryTrends = complaints.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getCategory().toString(),
                                                Collectors.counting()));
                trends.put("categoryTrends", categoryTrends);

                // Status distribution
                Map<String, Long> statusDistribution = complaints.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getStatus().toString(),
                                                Collectors.counting()));
                trends.put("statusDistribution", statusDistribution);

                return trends;
        }

        public Page<ComplaintResponse> getEscalatedComplaints(Pageable pageable) {
                // Find complaints that have escalation updates
                List<Complaint> allComplaints = complaintRepository.findAll();
                List<Complaint> escalatedComplaints = allComplaints.stream()
                                .filter(complaint -> complaint.getUpdates().stream()
                                                .anyMatch(update -> update.getComment() != null &&
                                                                update.getComment().toLowerCase()
                                                                                .contains("escalated")))
                                .collect(Collectors.toList());

                // Apply pagination
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), escalatedComplaints.size());
                List<Complaint> pageContent = escalatedComplaints.subList(start, end);

                List<ComplaintResponse> responseList = pageContent.stream()
                                .map(complaint -> complaintService.mapToResponse(complaint))
                                .collect(Collectors.toList());

                return new PageImpl<>(responseList, pageable, escalatedComplaints.size());
        }

        public Map<String, Object> getPerformanceMetrics() {
                Map<String, Object> metrics = new HashMap<>();

                // Overall metrics
                long totalComplaints = complaintRepository.count();
                long resolvedComplaints = complaintRepository.countByStatus(Complaint.Status.RESOLVED);

                metrics.put("totalComplaints", totalComplaints);
                metrics.put("resolvedComplaints", resolvedComplaints);
                metrics.put("resolutionRate",
                                totalComplaints > 0 ? (double) resolvedComplaints / totalComplaints * 100 : 0);

                // Performance by user
                List<User> adminUsers = userRepository.findByRole(User.Role.ADMIN);
                List<User> managerUsers = userRepository.findByRole(User.Role.MANAGER);
                List<User> supervisorUsers = userRepository.findByRole(User.Role.SUPERVISOR);

                List<User> users = new ArrayList<>();
                users.addAll(adminUsers);
                users.addAll(managerUsers);
                users.addAll(supervisorUsers);
                Map<String, Object> userPerformance = new HashMap<>();

                for (User user : users) {
                        List<Complaint> userComplaints = complaintRepository.findByAssignedTo(user);
                        long userResolved = userComplaints.stream()
                                        .mapToLong(c -> c.getStatus() == Complaint.Status.RESOLVED ? 1 : 0)
                                        .sum();

                        userPerformance.put(user.getFullName(), Map.of(
                                        "assigned", userComplaints.size(),
                                        "resolved", userResolved,
                                        "resolutionRate",
                                        userComplaints.size() > 0 ? (double) userResolved / userComplaints.size() * 100
                                                        : 0));
                }

                metrics.put("userPerformance", userPerformance);

                return metrics;
        }
}