package com.resolve.complaint.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public byte[] generateComplaintReportCSV(String startDate, String endDate, 
                                           String category, String status) {
        try {
            List<Complaint> complaints = getFilteredComplaints(startDate, endDate, category, status);
            
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);
            
            // Write header
            String[] header = {
                "Complaint ID", "Subject", "Description", "Category", "Priority", 
                "Status", "Submission Type", "Created Date", "Updated Date", 
                "Resolved Date", "Assigned To"
            };
            csvWriter.writeNext(header);
            
            // Write data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Complaint complaint : complaints) {
                String[] row = {
                    complaint.getComplaintId(),
                    complaint.getSubject(),
                    complaint.getDescription(),
                    complaint.getCategory().toString(),
                    complaint.getPriority().toString(),
                    complaint.getStatus().toString(),
                    complaint.getSubmissionType().toString(),
                    complaint.getCreatedAt().format(formatter),
                    complaint.getUpdatedAt().format(formatter),
                    complaint.getResolvedAt() != null ? complaint.getResolvedAt().format(formatter) : "",
                    complaint.getAssignedTo() != null ? complaint.getAssignedTo().getFullName() : "Unassigned"
                };
                csvWriter.writeNext(row);
            }
            
            csvWriter.close();
            return stringWriter.toString().getBytes();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV report", e);
        }
    }

    public byte[] generateComplaintReportPDF(String startDate, String endDate, 
                                           String category, String status) {
        try {
            List<Complaint> complaints = getFilteredComplaints(startDate, endDate, category, status);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Title
            document.add(new Paragraph("Complaint Report")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
            
            // Report parameters
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT));
            
            if (startDate != null || endDate != null) {
                String dateRange = "Date Range: ";
                if (startDate != null) dateRange += "From " + startDate + " ";
                if (endDate != null) dateRange += "To " + endDate;
                document.add(new Paragraph(dateRange).setFontSize(10));
            }
            
            if (category != null && !category.isEmpty()) {
                document.add(new Paragraph("Category: " + category).setFontSize(10));
            }
            
            if (status != null && !status.isEmpty()) {
                document.add(new Paragraph("Status: " + status).setFontSize(10));
            }
            
            document.add(new Paragraph("Total Complaints: " + complaints.size()).setFontSize(10).setBold());
            document.add(new Paragraph("\n"));
            
            // Create table
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 2, 2, 2, 2, 3}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Table headers
            table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Subject").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Category").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Priority").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Type").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Created").setBold()));
            
            // Table data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Complaint complaint : complaints) {
                table.addCell(new Cell().add(new Paragraph(complaint.getComplaintId()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(complaint.getSubject()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(complaint.getCategory().toString()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(complaint.getPriority().toString()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(complaint.getStatus().toString()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(complaint.getSubmissionType().toString()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(complaint.getCreatedAt().format(formatter)).setFontSize(8)));
            }
            
            document.add(table);
            
            // Summary statistics
            document.add(new Paragraph("\nSummary Statistics").setBold().setFontSize(14));
            
            // Status breakdown
            var statusBreakdown = complaints.stream()
                .collect(Collectors.groupingBy(
                    c -> c.getStatus().toString(),
                    Collectors.counting()));
            
            document.add(new Paragraph("Status Breakdown:").setBold());
            statusBreakdown.forEach((status1, count) -> 
                document.add(new Paragraph("  " + status1 + ": " + count)));
            
            // Category breakdown
            var categoryBreakdown = complaints.stream()
                .collect(Collectors.groupingBy(
                    c -> c.getCategory().toString(),
                    Collectors.counting()));
            
            document.add(new Paragraph("\nCategory Breakdown:").setBold());
            categoryBreakdown.forEach((cat, count) -> 
                document.add(new Paragraph("  " + cat + ": " + count)));
            
            // Priority breakdown
            var priorityBreakdown = complaints.stream()
                .collect(Collectors.groupingBy(
                    c -> c.getPriority().toString(),
                    Collectors.counting()));
            
            document.add(new Paragraph("\nPriority Breakdown:").setBold());
            priorityBreakdown.forEach((priority, count) -> 
                document.add(new Paragraph("  " + priority + ": " + count)));
            
            // Submission type breakdown
            var submissionTypeBreakdown = complaints.stream()
                .collect(Collectors.groupingBy(
                    c -> c.getSubmissionType().toString(),
                    Collectors.counting()));
            
            document.add(new Paragraph("\nSubmission Type Breakdown:").setBold());
            submissionTypeBreakdown.forEach((type, count) -> 
                document.add(new Paragraph("  " + type + ": " + count)));
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    private List<Complaint> getFilteredComplaints(String startDate, String endDate, 
                                                String category, String status) {
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate + "T00:00:00") : 
            LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? 
            LocalDateTime.parse(endDate + "T23:59:59") : 
            LocalDateTime.now();

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

        return complaints;
    }
}