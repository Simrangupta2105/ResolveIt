-- Online Complaint Portal Database Schema

CREATE DATABASE IF NOT EXISTS complaint_portal;
USE complaint_portal;

-- Users table for authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN', 'EMPLOYEE') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Complaints table
CREATE TABLE complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(20) UNIQUE NOT NULL,
    user_id BIGINT NULL, -- NULL for anonymous complaints
    subject VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category ENUM('SERVICE', 'BILLING', 'TECHNICAL', 'STAFF', 'FACILITY', 'OTHER') NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    status ENUM('NEW', 'UNDER_REVIEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'NEW',
    submission_type ENUM('PUBLIC', 'ANONYMOUS') NOT NULL,
    assigned_to BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL
);

-- Complaint attachments
CREATE TABLE complaint_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE
);

-- Status updates and timeline
CREATE TABLE complaint_updates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    status ENUM('NEW', 'UNDER_REVIEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL,
    comment TEXT,
    is_public BOOLEAN DEFAULT TRUE,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Employee access requests
CREATE TABLE employee_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    reviewed_by BIGINT NULL,
    FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

-- Insert default admin user
INSERT INTO users (username, email, password, full_name, role) VALUES 
('admin', 'admin@resolve.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Administrator', 'ADMIN');

-- Insert sample complaints for testing
INSERT INTO complaints (complaint_id, subject, description, category, priority, submission_type) VALUES 
('C2024001', 'Delayed Response Issue', 'The response time for my previous complaint was too long', 'SERVICE', 'MEDIUM', 'ANONYMOUS'),
('C2024002', 'Incorrect Information Provided', 'The information given was not accurate', 'STAFF', 'HIGH', 'PUBLIC'),
('C2024003', 'Service Disruption', 'Service was unavailable for extended period', 'TECHNICAL', 'URGENT', 'PUBLIC');

-- Insert status updates for sample complaints
INSERT INTO complaint_updates (complaint_id, status, comment, updated_by) VALUES 
(1, 'NEW', 'Complaint submitted successfully', 1),
(2, 'NEW', 'Complaint submitted successfully', 1),
(2, 'UNDER_REVIEW', 'Complaint is currently under review by our team', 1),
(3, 'NEW', 'Complaint submitted successfully', 1),
(3, 'UNDER_REVIEW', 'Investigation started', 1),
(3, 'IN_PROGRESS', 'Working on resolution', 1);