# ResolveIt - Online Complaint and Grievance Portal

A comprehensive web-based complaint management system built with Spring Boot and React, designed for organizations to efficiently handle customer complaints and grievances.

## Features

### Core Functionality
- **Complaint Submission**: Submit complaints with detailed information, categories, and file attachments
- **Dual Submission Types**: 
  - **Public**: Linked to user accounts with full tracking capabilities
  - **Anonymous**: Privacy-protected submissions with masked IDs
- **Real-time Status Tracking**: Monitor complaint progress through various stages
- **File Attachments**: Support for images and documents (PDF, DOC, JPG, PNG)
- **Role-based Access Control**: Different interfaces for users and administrators

### User Features
- **User Dashboard**: View personal complaint history and status updates
- **Complaint Status Check**: Track complaint progress with detailed timeline
- **Category-based Submission**: Organized complaint categories (Service, Billing, Technical, Staff, Facility, Other)
- **Priority Levels**: Low, Medium, High, and Urgent priority settings
- **Theme Support**: Light and dark mode toggle

### Administrative Features
- **Admin Dashboard**: Comprehensive overview with statistics and metrics
- **Complaint Management**: Assign, update status, and manage all complaints
- **User Management**: Handle user accounts and role assignments
- **Escalation System**: Escalate complaints to higher authorities with notifications
- **Reports & Analytics**: Generate detailed reports with filtering options
- **Export Functionality**: Export complaint data in CSV and PDF formats

### Advanced Features
- **JWT Authentication**: Secure token-based authentication system
- **Responsive Design**: Professional UI that works on all devices
- **Real-time Updates**: Dynamic status updates and notifications
- **Search & Filtering**: Advanced filtering by status, category, priority, and date
- **Audit Trail**: Complete history of complaint updates and changes

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (in-memory) / MySQL support
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18
- **Routing**: React Router DOM
- **Styling**: Custom CSS with CSS Variables
- **HTTP Client**: Axios
- **State Management**: React Context API

### Additional Libraries
- **CSV Export**: OpenCSV
- **PDF Generation**: iText7
- **File Upload**: Spring Boot File Upload
- **Validation**: Bean Validation (JSR-303)

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- npm or yarn package manager
- Git

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd ResolveIt-App
```

### 2. Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```
The backend will start on `http://localhost:8080`

### 3. Frontend Setup
```bash
cd frontend
npm install
npm start
```
The frontend will start on `http://localhost:3001`

### 4. Access the Application
- **Main Application**: http://localhost:3001
- **Admin Dashboard**: http://localhost:3001/admin/dashboard
- **API Documentation**: http://localhost:8080/swagger-ui.html (if configured)

## Default Users

The application comes with pre-configured users for testing:

### Admin User
- **Username**: `admin`
- **Password**: `password`
- **Role**: Administrator
- **Access**: Full system access, admin dashboard, user management

### Regular User
- **Username**: `user`
- **Password**: `password`
- **Role**: User
- **Access**: Submit complaints, view personal complaints

## Sample Data

The application automatically loads sample data including:
- 6 sample complaints (3 anonymous, 3 public)
- Various complaint categories and priorities
- Different status examples
- Sample user accounts

## Configuration

### Database Configuration
The application uses H2 in-memory database by default. To use MySQL:

1. Update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/complaint_portal
    username: your_username
    password: your_password
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
```

2. Add MySQL dependency to `pom.xml` (already included)

### JWT Configuration
JWT settings can be configured in `application.yml`:
```yaml
app:
  jwtSecret: your-secret-key
  jwtExpirationMs: 86400000
```

### File Upload Configuration
Configure file upload limits in `application.yml`:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

## API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Complaints
- `GET /api/complaints` - Get all complaints (paginated)
- `POST /api/complaints` - Submit new complaint
- `GET /api/complaints/{id}` - Get complaint by ID
- `PUT /api/complaints/{id}/status` - Update complaint status

### Admin Endpoints
- `GET /api/admin/dashboard/stats` - Dashboard statistics
- `GET /api/admin/complaints` - Get filtered complaints
- `PUT /api/admin/complaints/{id}/assign` - Assign complaint
- `POST /api/admin/complaints/{id}/escalate` - Escalate complaint
- `GET /api/admin/reports/export/csv` - Export CSV report
- `GET /api/admin/reports/export/pdf` - Export PDF report

## UI Features

### Professional Design
- Clean, business-appropriate interface
- Responsive design for all screen sizes
- Professional color scheme with blue primary colors
- Subtle shadows and modern typography

### Theme Support
- Light mode (default)
- Dark mode toggle
- System preference detection
- Persistent theme selection

### Accessibility
- Keyboard navigation support
- Screen reader friendly
- High contrast ratios
- Focus indicators

## Reports and Analytics

### Dashboard Statistics
- Total complaints count
- Open complaints
- Resolved complaints
- Average resolution time

### Export Options
- **CSV Export**: Complete data export for spreadsheet analysis
- **PDF Export**: Professional reports with statistics and summaries
- **Filtering**: Export by date range, category, status, and priority

### Report Features
- Summary statistics
- Category breakdown
- Status distribution
- Priority analysis
- Submission type metrics

## Security Features

- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption
- CORS configuration
- Input validation and sanitization
- File upload security

## Deployment

### Development
Both backend and frontend include development scripts:
- Backend: `./mvnw spring-boot:run`
- Frontend: `npm start`

### Production Build
```bash
# Frontend production build
cd frontend
npm run build

# Backend JAR build
cd backend
./mvnw clean package
java -jar target/complaint-portal-1.0.0.jar
```

### Docker Support (Optional)
Create Dockerfile for containerized deployment:
```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim
COPY target/complaint-portal-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Testing

### Backend Testing
```bash
cd backend
./mvnw test
```

### Frontend Testing
```bash
cd frontend
npm test
```

## Usage Guide

### For End Users
1. **Submit Complaint**: Navigate to "Submit Complaint" and fill out the form
2. **Choose Submission Type**: Select Public (with tracking) or Anonymous (privacy-protected)
3. **Track Status**: Use "Complaint Status" to monitor progress
4. **View History**: Access "My Complaints" to see all your submissions

### For Administrators
1. **Access Admin Dashboard**: Login with admin credentials
2. **Manage Complaints**: View, assign, and update complaint statuses
3. **Generate Reports**: Use the Reports section to export data
4. **Escalate Issues**: Use the escalation feature for complex complaints
5. **Monitor Performance**: Review dashboard statistics and metrics

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the API endpoints
- Examine the sample data and configurations

## Version History

### v1.0.0 (Current)
- Initial release with full complaint management system
- JWT authentication and role-based access
- Anonymous and public submission types
- Admin dashboard with comprehensive management tools
- CSV and PDF export functionality
- Professional UI with theme support
- Responsive design for all devices

## Email Notifications ✅ IMPLEMENTED

The system includes comprehensive email notifications for:
- Complaint status changes
- Assignment notifications to employees  
- Escalation notifications
- Personal notes between admin and employees

### Current Configuration
- **Sender Email**: anonymous200091@gmail.com
- **SMTP Server**: Gmail (smtp.gmail.com:587)
- **Authentication**: Requires Gmail App Password

### Setting Up Real Email Delivery

#### Quick Setup
1. **Generate Gmail App Password**:
   - Go to Google Account Security settings
   - Enable 2-Factor Authentication
   - Generate App Password for "Mail" application
   - Copy the 16-character password

2. **Configure and Start**:
   ```bash
   # Run the setup script
   powershell -ExecutionPolicy Bypass -File setup-gmail-app-password.ps1
   ```

#### Testing Email Delivery
```bash
# Test real email notifications
powershell -ExecutionPolicy Bypass -File test-real-email.ps1
```

### Email Features
- **Status Updates**: Users receive emails when complaint status changes
- **Assignment Notifications**: Employees get notified when assigned complaints
- **Escalation Alerts**: Users are informed when complaints are escalated
- **Personal Notes**: Email notifications for admin-employee communications
- **Backup Files**: All emails are saved to `backend/emails/` for verification

For detailed setup instructions, see `GMAIL_APP_PASSWORD_SETUP.md`.

## Future Enhancements

- Advanced reporting with charts
- Mobile application
- Integration with external systems
- Advanced workflow management
- Multi-language support
- Advanced search capabilities
- Automated complaint routing

---

**ResolveIt** - Streamlining complaint management for better customer service.