# Test Credentials

## User Accounts

### Admin Account
- **Username**: `admin`
- **Email**: `admin@resolve.com`
- **Password**: `password`
- **Role**: Administrator
- **Access**: Admin Dashboard, Manage Complaints, Employee Requests, Reports

### Employee Account
- **Username**: `employee`
- **Email**: `dishasah924@gmail.com`
- **Password**: `password`
- **Role**: Employee
- **Access**: Employee Dashboard, Assigned Complaints, Status Updates

### Employee Account 1
- **Username**: `emp1`
- **Email**: `emp1@resolve.com`
- **Password**: `password`
- **Role**: Employee
- **Access**: Employee Dashboard, All Admin Features, Complaint Management

### Employee Account 2
- **Username**: `emp2`
- **Email**: `emp2@resolve.com`
- **Password**: `password`
- **Role**: Employee
- **Access**: Employee Dashboard, All Admin Features, Complaint Management

### Employee Account 3
- **Username**: `emp3`
- **Email**: `emp3@resolve.com`
- **Password**: `password`
- **Role**: Employee
- **Access**: Employee Dashboard, All Admin Features, Complaint Management

### Manager Account
- **Username**: `manager`
- **Email**: `manager@resolve.com`
- **Password**: `password`
- **Role**: Manager
- **Access**: Team Management, Complaint Oversight, Escalation Handling

### Supervisor Account
- **Username**: `supervisor`
- **Email**: `supervisor@resolve.com`
- **Password**: `password`
- **Role**: Supervisor
- **Access**: Department Oversight, Complaint Resolution, Staff Coordination

### Regular User Accounts

#### User 1
- **Username**: `user`
- **Email**: `simrangupta21007@gmail.com`
- **Password**: `password`
- **Role**: Regular User
- **Access**: Submit Complaints, View My Complaints, User Dashboard

#### User 2 (Test Account)
- **Username**: `user1`
- **Email**: `anonymous200091@gmail.com`
- **Password**: `password`
- **Role**: Regular User
- **Access**: Submit Complaints, View My Complaints, User Dashboard

## Email Notification Testing

### Email Recipients for Testing:
- **simrangupta21007@gmail.com** - Linked to `user` account
- **anonymous200091@gmail.com** - Linked to `user1` account

### Email Notifications Are Sent When:
1. **Complaint Status Changes** - User receives email when admin/employee updates status
2. **Complaint Assignment** - Employee receives email when complaint is assigned to them
3. **Complaint Escalation** - Higher authorities receive escalation notifications
4. **New Complaint Submission** - Confirmation email sent to complainant (for public complaints)

### Testing Email Notifications:
1. Login as `user` (simrangupta21007@gmail.com) and submit a public complaint
2. Login as admin/employee and change the complaint status
3. Check simrangupta21007@gmail.com for status update email
4. Assign complaint to an employee - employee receives assignment email
5. Login as `user1` (anonymous200091@gmail.com) and submit a public complaint
6. Update status and check anonymous200091@gmail.com for notifications

## Login Instructions
You can now log in using **either username OR email address**:
- **Employee**: `dishasah924@gmail.com` or `employee` with password `password`
- **Employee 1**: `emp1@resolve.com` or `emp1` with password `password`
- **Employee 2**: `emp2@resolve.com` or `emp2` with password `password`
- **Employee 3**: `emp3@resolve.com` or `emp3` with password `password`
- **Admin**: `admin@resolve.com` or `admin` with password `password`

## Login Flow

### For Admin Users:
1. Login with admin credentials (username: `admin` or email: `admin@resolve.com`)
2. Automatically redirected to Admin Dashboard
3. Can manage all complaints and users
4. Real-time notifications for all system activities

### For Employee Users:
1. Login with any employee credentials:
   - `employee` / `password` (or email: `dishasah924@gmail.com`)
   - `emp1` / `password` (or email: `emp1@resolve.com`)
   - `emp2` / `password` (or email: `emp2@resolve.com`)
   - `emp3` / `password` (or email: `emp3@resolve.com`)
2. Redirected to Employee Dashboard
3. **Full admin access**: Can view all complaints, manage assignments, access reports
4. Can update complaint status for any complaint
5. Real-time notifications for all system activities

### For Regular Users:
1. Login with user credentials
2. Redirected to Home page with User Dashboard
3. Can submit PUBLIC complaints (linked to account)
4. Can view their own complaints
5. Receive email notifications for status changes

### For Anonymous Users:
1. No login required
2. Can submit ANONYMOUS complaints
3. Can track complaints by ID
4. Can view public complaint listings

## Testing Scenarios

### Employee Workflow:
1. Login as any employee account:
   - `employee` / `password` (or use email `dishasah924@gmail.com`)
   - `emp1` / `password` (or use email `emp1@resolve.com`)
   - `emp2` / `password` (or use email `emp2@resolve.com`)
   - `emp3` / `password` (or use email `emp3@resolve.com`)
2. Go to Employee Dashboard
3. **Full admin capabilities**: View all complaints, manage assignments, access reports
4. Update complaint status for any complaint
5. **Automatic email notifications sent to complainants**

### Public Complaint Submission:
1. Login as `user` / `password` (email: `simrangupta21007@gmail.com`)
2. Go to Submit Complaint
3. Select "Public" submission type
4. Fill form and submit
5. Complaint will be linked to user account
6. **Can be tracked by complaint ID**
7. **Real-time dashboard updates**
8. **Email notifications sent to simrangupta21007@gmail.com**

### Anonymous Complaint Submission:
1. No login required
2. Go to Submit Complaint
3. Select "Anonymous" submission type
4. Fill form and submit
5. Complaint will be anonymous (no user association)
6. **Cannot be tracked by ID for privacy protection**
7. **Real-time dashboard updates**

### Admin Management:
1. Login as `admin` / `password` (or use email `admin@resolve.com`)
2. Access Admin Dashboard
3. View all complaints (both public and anonymous)
4. Manage complaint status and assignments
5. **Can view anonymous complaints in admin panel**
6. **Real-time notifications for all activities**
7. **7-day escalation rule enforced**

## Advanced Features

### Real-time Updates:
- ✅ Dashboard updates automatically when new complaints are submitted
- ✅ Live notifications for status changes, assignments, and escalations
- ✅ WebSocket-based real-time communication
- ✅ Connection status indicator

### Email Notifications:
- ✅ Automatic emails when complaint status changes
- ✅ Assignment notifications to employees
- ✅ Escalation notifications to higher authorities
- ✅ SMTP configuration ready for production

### 7-Day Escalation Rule:
- ✅ Complaints can only be escalated after 7 days from creation
- ✅ Frontend shows escalation eligibility status
- ✅ Backend enforces the rule with proper error messages

### Private Notes System:
- ✅ Staff can add private notes visible only to employees/admins
- ✅ Public notes visible to complainants
- ✅ Clear distinction in UI between private and public notes

## Privacy Protection

### Anonymous Complaints:
- ✅ Cannot be tracked by complaint ID (privacy protected)
- ✅ Only visible to admin users in management interface
- ✅ ID is masked as "ANONYMOUS-****" in public listings
- ✅ Submitter identity is completely protected

### Public Complaints:
- ✅ Can be tracked by complaint ID
- ✅ Linked to user account for full tracking
- ✅ Visible in user's "My Complaints" section
- ✅ Full transparency and tracking available
- ✅ Email notifications for status updates

## Email Notification System Summary

### ✅ Fully Implemented Features:
- **Status Change Notifications**: Users receive emails when complaint status changes
- **Assignment Notifications**: Employees receive emails when assigned complaints  
- **Escalation Notifications**: Users receive emails when complaints are escalated
- **Resolution Notifications**: Users receive emails when complaints are resolved
- **Personal Note Notifications**: Employees receive emails for personal notes from admin

### 📧 Email Configuration:
- **SMTP Server**: smtp.gmail.com:587 with STARTTLS
- **From Address**: complaint.portal.system@gmail.com
- **Status**: Configured and working (requires real credentials for actual email sending)

### 🔒 Privacy Protection:
- **PUBLIC complaints**: Full email notifications sent to users
- **ANONYMOUS complaints**: No emails sent to protect user privacy
- **Employee notifications**: Only work-related emails sent

### 🧪 Testing Email Notifications:
1. Run test script: `./test-email-notifications.ps1`
2. Login as admin and change any PUBLIC complaint status
3. Check backend logs for email sending confirmation
4. Email will be attempted to be sent to user's email address

### 📋 Test Email Addresses:
- **simrangupta21007@gmail.com** (user account)
- **anonymous200091@gmail.com** (user1 account)
- **dishasah924@gmail.com** (employee account)

**Note**: To enable actual email delivery, configure real SMTP credentials in `application.yml` or set environment variables `MAIL_USERNAME` and `MAIL_PASSWORD`.