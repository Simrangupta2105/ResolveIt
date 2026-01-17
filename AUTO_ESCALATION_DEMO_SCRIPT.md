# Auto-Escalation Demo Script for Presentation

## 🎯 Demo Setup Complete!

### **Demo Complaint Created:**
- **Complaint ID**: C2026011 (or similar)
- **Subject**: "Old Test Complaint for Auto-Escalation"
- **Status**: NEW (ready for escalation)
- **Age**: 10+ days old (eligible for auto-escalation)
- **Type**: PUBLIC (trackable complaint)

---

## 🎬 Live Demo Script (3-4 minutes)

### **Step 1: Show the Old Complaint (30 seconds)**
```
"Let me show you our auto-escalation feature. Here we have a complaint 
that's been sitting unresolved for over 7 days..."

1. Login as admin (admin/password)
2. Go to Admin Dashboard → View Complaints
3. Point out the old complaint with NEW status
4. Mention: "This complaint is now eligible for auto-escalation"
```

### **Step 2: Explain Auto-Escalation Rules (30 seconds)**
```
"Our system automatically escalates complaints that meet these criteria:
- Older than 7 days
- Status is not RESOLVED, CLOSED, or already ESCALATED  
- PUBLIC complaints only (for privacy protection)
- Runs automatically every hour in production"
```

### **Step 3: Trigger Manual Escalation (1 minute)**
```
"For demo purposes, I'll trigger the auto-escalation manually..."

1. In Admin Dashboard, scroll to bottom
2. Click "Trigger Auto-Escalation" button (if available in UI)
   OR explain: "In production, this runs automatically"
3. Show the system processing
4. Refresh the complaints list
```

### **Step 4: Show Escalation Results (1 minute)**
```
"Now let's see what happened..."

1. Refresh complaints list
2. Show the complaint status changed to "ESCALATED"
3. Show it's now assigned to "Disha Shah" (Senior Manager)
4. Explain: "The system automatically assigned it to senior management"
```

### **Step 5: Demonstrate Email Notifications (1 minute)**
```
"The system also sent email notifications..."

1. Explain: "Two emails were automatically sent:"
   - "One to the original complainant (Simran Gupta)"
   - "One to the senior manager (Disha Shah)"
2. Show email files in backend/emails/ folder (if accessible)
3. Mention: "These are real Gmail emails sent via SMTP"
```

---

## 🎯 Key Talking Points During Demo

### **Business Value:**
- **"No complaints fall through the cracks"**
- **"Senior management gets automatic visibility"**
- **"Customers are kept informed throughout the process"**
- **"Reduces manual oversight and intervention"**

### **Technical Implementation:**
- **"Scheduled job runs every hour using Spring's @Scheduled"**
- **"Database queries find eligible complaints automatically"**
- **"Real Gmail SMTP integration for professional notifications"**
- **"WebSocket updates provide real-time UI refresh"**

### **Enterprise Features:**
- **"Configurable escalation rules and timeframes"**
- **"Role-based assignment to appropriate managers"**
- **"Audit trail maintains complete complaint history"**
- **"Email templates are professional and informative"**

---

## 🚀 Manual Demo Commands (Backup)

If you need to trigger escalation via API during demo:

```bash
# Login as admin first, then:
POST http://localhost:8080/api/admin/trigger-auto-escalation
Headers: Authorization: Bearer {admin-token}
```

Or via PowerShell:
```powershell
$escalationResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/trigger-auto-escalation" -Method POST -Headers $adminHeaders
```

---

## 📧 Email Notification Details

### **Email to User (Complainant):**
- **Subject**: "Your Complaint Has Been Escalated - C2026011"
- **Content**: Professional notification with complaint details
- **Recipient**: simrangupta21007@gmail.com

### **Email to Senior Manager:**
- **Subject**: "URGENT: Complaint Auto-Escalated - C2026011"  
- **Content**: Detailed complaint information for management action
- **Recipient**: dishasah924@gmail.com (Disha Shah)

---

## 🎯 Demo Variations

### **For Technical Audience:**
- Show the actual code in AutoEscalationService.java
- Explain the @Scheduled annotation and cron expressions
- Discuss database queries and JPA relationships
- Mention scalability and performance considerations

### **For Business Audience:**
- Focus on workflow automation and efficiency
- Emphasize customer communication and transparency
- Highlight management oversight and control
- Discuss compliance and audit trail benefits

### **For Mixed Audience:**
- Start with business benefits
- Show the live demo
- Briefly mention technical implementation
- End with scalability and future enhancements

---

## 💡 Pro Tips for Demo

1. **Practice the flow** - Know exactly where to click
2. **Have backup data** - Multiple old complaints ready
3. **Explain while doing** - Don't just click silently
4. **Show real emails** - Open Gmail if possible
5. **Mention production** - "This runs automatically every hour"
6. **Handle questions** - Be ready to explain technical details

---

## 🎬 Demo Closing Statement

*"This auto-escalation system ensures that no customer complaint gets forgotten. It automatically identifies aging complaints, escalates them to senior management, and keeps everyone informed through professional email notifications. This is the kind of enterprise-level automation that improves both customer satisfaction and operational efficiency."*

---

**Your demo complaint is ready! The system now has a complaint that's over 7 days old and eligible for auto-escalation demonstration.**