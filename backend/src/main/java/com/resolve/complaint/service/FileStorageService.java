package com.resolve.complaint.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;
    
    // Allowed file extensions
    private static final String[] ALLOWED_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", // Images
        ".pdf", ".doc", ".docx", ".txt", ".rtf", // Documents
        ".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv" // Videos
    };
    
    // Maximum file size (50MB)
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    public String storeFile(MultipartFile file, Long complaintId) {
        try {
            // Validate file
            validateFile(file);
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, "complaints", complaintId.toString());
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Store file
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path for storage in database
            String relativePath = "complaints/" + complaintId + "/" + uniqueFilename;
            System.out.println("File stored at: " + targetLocation.toString());
            System.out.println("Relative path: " + relativePath);
            return relativePath;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot store empty file");
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum limit of 50MB");
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("Invalid file name");
        }
        
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(fileExtension)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            throw new RuntimeException("File type not supported. Allowed types: images, documents, and videos");
        }
        
        // Additional MIME type validation for security
        String contentType = file.getContentType();
        if (contentType != null) {
            if (!contentType.startsWith("image/") && 
                !contentType.startsWith("video/") && 
                !contentType.startsWith("application/") && 
                !contentType.startsWith("text/")) {
                throw new RuntimeException("Invalid file type detected");
            }
        }
    }

    public byte[] loadFile(String filePath) {
        try {
            // If filePath is relative, prepend uploadDir
            Path path;
            if (filePath.startsWith("/") || filePath.contains(":")) {
                // Absolute path
                path = Paths.get(filePath);
            } else {
                // Relative path
                path = Paths.get(uploadDir, filePath);
            }
            
            System.out.println("Loading file from: " + path.toString());
            
            if (!Files.exists(path)) {
                System.err.println("File not found: " + path.toString());
                throw new RuntimeException("File not found: " + path.toString());
            }
            
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            System.err.println("Could not read file: " + filePath + " - " + ex.getMessage());
            throw new RuntimeException("Could not read file: " + filePath, ex);
        }
    }
}