package com.resolve.complaint.dto;

public class DashboardStatsResponse {
    private long totalComplaints;
    private long openComplaints;
    private long resolvedComplaints;
    private long averageResolutionTime;

    // Constructors
    public DashboardStatsResponse() {}

    public DashboardStatsResponse(long totalComplaints, long openComplaints, 
                                long resolvedComplaints, long averageResolutionTime) {
        this.totalComplaints = totalComplaints;
        this.openComplaints = openComplaints;
        this.resolvedComplaints = resolvedComplaints;
        this.averageResolutionTime = averageResolutionTime;
    }

    // Getters and Setters
    public long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }

    public long getOpenComplaints() { return openComplaints; }
    public void setOpenComplaints(long openComplaints) { this.openComplaints = openComplaints; }

    public long getResolvedComplaints() { return resolvedComplaints; }
    public void setResolvedComplaints(long resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

    public long getAverageResolutionTime() { return averageResolutionTime; }
    public void setAverageResolutionTime(long averageResolutionTime) { this.averageResolutionTime = averageResolutionTime; }
}