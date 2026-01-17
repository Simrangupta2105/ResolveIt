# Complaint Portal System - Clean Project Structure

## 📁 Project Overview
A complete full-stack complaint management system with Spring Boot backend and React frontend.

## 🏗️ Architecture
- **Backend**: Spring Boot with H2 Database
- **Frontend**: React with Material-UI inspired design
- **Authentication**: JWT-based security
- **Email**: Gmail SMTP integration
- **Real-time**: WebSocket notifications

## 📂 Directory Structure

```
complaint-portal/
├── backend/                          # Spring Boot Application
│   ├── src/main/java/com/resolve/complaint/
│   │   ├── ComplaintPortalApplication.java
│   │   ├── controller/               # REST Controllers
│   │   ├── service/                  # Business Logic
│   │   ├── model/                    # JPA Entities
│   │   ├── repository/               # Data Access Layer
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── security/                 # JWT & Security Config
│   │   └── config/                   # Configuration Classes
│   ├── src/main/resources/
│   │   └── application.yml           # Application Configuration
│   ├── pom.xml                       # Maven Dependencies
│   └── mvnw.cmd                      # Maven Wrapper
├── frontend/                         # React Application
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/               # Reusable Components
│   │   ├── context/                  # React Context (Auth, Theme)
│   │   ├── pages/                    # Page Components
│   │   ├── services/                 # API Services
│   │   ├── hooks/                    # Custom React Hooks
│   │   ├── config/                   # Configuration
│   │   ├── App.js                    # Main App Component
│   │   └── index.css                 # Global Styles
│   ├── package.json                  # NPM Dependencies
│   └── vercel.json                   # Deployment Config
├── database/
│   └── schema.sql                    # Database Schema
├── README.md                         # Project Documentation
├── COMPREHENSIVE_IMPLEMENTATION.md   # Technical Details
├── DEPLOYMENT_GUIDE.md              # Deployment Instructions
├── TEST_CREDENTIALS.md              # Login Credentials
├── ESSENTIAL_FILES_SUMMARY.md       # File Organization Guide
└── vercel.json                      # Root Deployment Config
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 16+
- Maven 3.6+

### Development Setup
```bash
# Clone and navigate to project
cd complaint-portal

# Start Backend (Terminal 1)
cd backend
./mvnw.cmd spring-boot:run

# Start Frontend (Terminal 2)
cd frontend
npm install
npm start
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

## 👥 Default Users
- **Admin**: admin / password
- **User**: user / password  
- **Employee**: emp1, emp2 / password
- **Manager**: disha / password

## ✨ Key Features
- ✅ Role-based authentication & authorization
- ✅ Complaint submission (Public/Anonymous)
- ✅ Admin dashboard with statistics
- ✅ Employee dashboard with assigned complaints
- ✅ Personal notes system (Admin → Employee)
- ✅ Auto-escalation system (7-day rule)
- ✅ Email notifications (Gmail SMTP)
- ✅ Real-time WebSocket updates
- ✅ CSV/PDF export functionality
- ✅ Professional UI with dark/light themes

## 🔧 Configuration
All configuration is in `backend/src/main/resources/application.yml`:
- Database settings
- JWT configuration  
- Email SMTP settings
- File upload limits

## 📧 Email Setup
The system uses Gmail SMTP. Update these properties in `application.yml`:
```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-password
```

## 🚀 Production Deployment
See `DEPLOYMENT_GUIDE.md` for detailed deployment instructions including:
- Vercel frontend deployment
- Backend deployment options
- Environment configuration
- Database setup

## 📝 Development Notes
- Backend uses H2 in-memory database (auto-creates tables)
- Frontend uses Axios for API calls
- WebSocket connection for real-time updates
- JWT tokens for stateless authentication
- Email files saved to `backend/emails/` for verification