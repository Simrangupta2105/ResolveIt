package com.resolve.complaint.config;

import com.resolve.complaint.model.User;
import com.resolve.complaint.model.Complaint;
import com.resolve.complaint.repository.UserRepository;
import com.resolve.complaint.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataLoader: Starting data initialization...");

        // Create admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            System.out.println("DataLoader: Creating admin user...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin2@yopmail.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setFullName("System Administrator");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("DataLoader: Admin user created successfully!");
        } else {
            System.out.println("DataLoader: Admin user already exists, updating email...");
            User existingAdmin = userRepository.findByUsername("admin").get();
            existingAdmin.setEmail("admin2@yopmail.com");
            existingAdmin.setFullName("System Administrator");
            userRepository.save(existingAdmin);
            System.out.println("DataLoader: Admin user email updated!");
        }

        // Create sample user if not exists
        if (userRepository.findByUsername("user").isEmpty()) {
            System.out.println("DataLoader: Creating sample user...");
            User user = new User();
            user.setUsername("user");
            user.setEmail("simrangupta21007@gmail.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setFullName("Simran Gupta");
            user.setRole(User.Role.USER);
            userRepository.save(user);
            System.out.println("DataLoader: Sample user created successfully!");
        } else {
            System.out.println("DataLoader: Sample user already exists, updating email...");
            User existingUser = userRepository.findByUsername("user").get();
            existingUser.setEmail("simrangupta21007@gmail.com");
            existingUser.setFullName("Simran Gupta");
            userRepository.save(existingUser);
            System.out.println("DataLoader: Sample user email updated!");
        }

        // Create user1 for testing if not exists
        if (userRepository.findByUsername("user1").isEmpty()) {
            System.out.println("DataLoader: Creating user1 for testing...");
            User user1 = new User();
            user1.setUsername("user1");
            user1.setEmail("testuser1@yopmail.com");
            user1.setPassword(passwordEncoder.encode("password"));
            user1.setFullName("Test User 1");
            user1.setRole(User.Role.USER);
            userRepository.save(user1);
            System.out.println("DataLoader: User1 created successfully!");
        } else {
            System.out.println("DataLoader: User1 already exists, updating email...");
            User existingUser1 = userRepository.findByUsername("user1").get();
            existingUser1.setEmail("testuser1@yopmail.com");
            existingUser1.setFullName("Test User 1");
            userRepository.save(existingUser1);
            System.out.println("DataLoader: User1 email updated!");
        }

        // Create manager user if not exists
        if (userRepository.findByUsername("manager").isEmpty()) {
            System.out.println("DataLoader: Creating manager user...");
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@resolve.com");
            manager.setPassword(passwordEncoder.encode("password"));
            manager.setFullName("Department Manager");
            manager.setRole(User.Role.MANAGER);
            userRepository.save(manager);
            System.out.println("DataLoader: Manager user created successfully!");
        } else {
            System.out.println("DataLoader: Manager user already exists.");
        }

        // Create supervisor user if not exists
        if (userRepository.findByUsername("supervisor").isEmpty()) {
            System.out.println("DataLoader: Creating supervisor user...");
            User supervisor = new User();
            supervisor.setUsername("supervisor");
            supervisor.setEmail("supervisor@resolve.com");
            supervisor.setPassword(passwordEncoder.encode("password"));
            supervisor.setFullName("Team Supervisor");
            supervisor.setRole(User.Role.SUPERVISOR);
            userRepository.save(supervisor);
            System.out.println("DataLoader: Supervisor user created successfully!");
        } else {
            System.out.println("DataLoader: Supervisor user already exists.");
        }

        // Create senior manager user (Disha Shah) for testing if not exists
        if (userRepository.findByEmail("dishasah924@gmail.com").isEmpty()) {
            System.out.println("DataLoader: Creating senior manager user (Disha Shah)...");
            User seniorManager = new User();
            seniorManager.setUsername("disha");
            seniorManager.setEmail("dishasah924@gmail.com");
            seniorManager.setPassword(passwordEncoder.encode("password"));
            seniorManager.setFullName("Disha Shah");
            seniorManager.setRole(User.Role.MANAGER);
            userRepository.save(seniorManager);
            System.out.println("DataLoader: Senior Manager (Disha Shah) created successfully!");
        } else {
            System.out.println("DataLoader: Senior Manager (Disha Shah) already exists.");
        }

        // Create emp1 user if not exists
        if (userRepository.findByUsername("emp1").isEmpty()) {
            System.out.println("DataLoader: Creating emp1 user...");
            User emp1 = new User();
            emp1.setUsername("emp1");
            emp1.setEmail("emp1@resolve.com");
            emp1.setPassword(passwordEncoder.encode("password"));
            emp1.setFullName("Employee One");
            emp1.setRole(User.Role.EMPLOYEE);
            userRepository.save(emp1);
            System.out.println("DataLoader: Emp1 user created successfully!");
        } else {
            System.out.println("DataLoader: Emp1 user already exists.");
        }

        // Create emp2 user if not exists
        if (userRepository.findByUsername("emp2").isEmpty()) {
            System.out.println("DataLoader: Creating emp2 user...");
            User emp2 = new User();
            emp2.setUsername("emp2");
            emp2.setEmail("emp2@resolve.com");
            emp2.setPassword(passwordEncoder.encode("password"));
            emp2.setFullName("Employee Two");
            emp2.setRole(User.Role.EMPLOYEE);
            userRepository.save(emp2);
            System.out.println("DataLoader: Emp2 user created successfully!");
        } else {
            System.out.println("DataLoader: Emp2 user already exists.");
        }

        // Create emp3 user if not exists
        if (userRepository.findByUsername("emp3").isEmpty()) {
            System.out.println("DataLoader: Creating emp3 user...");
            User emp3 = new User();
            emp3.setUsername("emp3");
            emp3.setEmail("emp3@resolve.com");
            emp3.setPassword(passwordEncoder.encode("password"));
            emp3.setFullName("Employee Three");
            emp3.setRole(User.Role.EMPLOYEE);
            userRepository.save(emp3);
            System.out.println("DataLoader: Emp3 user created successfully!");
        } else {
            System.out.println("DataLoader: Emp3 user already exists.");
        }

        // Create sample complaints - force recreation for previous dates
        System.out.println("DataLoader: Clearing existing complaints and creating new ones with previous dates...");

        // Clear existing complaints to ensure fresh data with previous dates
        complaintRepository.deleteAll();

        if (true) { // Always create new complaints with previous dates
            System.out.println("DataLoader: Creating realistic sample complaints...");

            // Get users for public complaints
            User user = userRepository.findByUsername("user").orElse(null);
            User user1 = userRepository.findByUsername("user1").orElse(null);
            User emp1 = userRepository.findByUsername("emp1").orElse(null);
            User emp2 = userRepository.findByUsername("emp2").orElse(null);

            // Complaint 1 - Recent anonymous complaint
            Complaint complaint1 = new Complaint();
            complaint1.setComplaintId("C2025001");
            complaint1.setSubject("Defective Product Received - Smartphone Screen Cracked");
            complaint1.setDescription(
                    "I ordered a brand new smartphone (Model XYZ-123) on December 28th, 2024, and received it on January 3rd, 2025. Upon opening the package, I discovered that the screen was severely cracked and the device would not turn on. The packaging appeared to be damaged during shipping. I need an immediate replacement as this was a gift for my daughter. Order number: ORD789456123. I have photos of the damaged device and packaging as evidence.");
            complaint1.setCategory(Complaint.Category.OTHER);
            complaint1.setPriority(Complaint.Priority.HIGH);
            complaint1.setSubmissionType(Complaint.SubmissionType.ANONYMOUS);
            complaint1.setStatus(Complaint.Status.NEW);
            complaint1.setCreatedAt(LocalDateTime.of(2024, 12, 28, 14, 30)); // December 28, 2024
            complaintRepository.save(complaint1);

            // Complaint 2 - Public complaint from Simran
            Complaint complaint2 = new Complaint();
            complaint2.setComplaintId("C2025002");
            complaint2.setSubject("Unauthorized Credit Card Charges - $347.89");
            complaint2.setDescription(
                    "Dear Customer Service Team, I noticed unauthorized charges on my credit card statement for December 2024. There are three suspicious transactions: 1) $127.50 on Dec 15th for 'Premium Subscription' that I never signed up for, 2) $95.39 on Dec 22nd for 'Digital Services' which I did not purchase, and 3) $125.00 on Dec 28th labeled as 'Processing Fee' with no explanation. I have contacted my bank and they advised me to file a complaint with your company first. Please investigate these charges immediately and provide a full refund. My account number is ACC-789456123 and card ending in 4567.");
            complaint2.setCategory(Complaint.Category.BILLING);
            complaint2.setPriority(Complaint.Priority.URGENT);
            complaint2.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint2.setUser(user);
            complaint2.setStatus(Complaint.Status.UNDER_REVIEW);
            complaint2.setCreatedAt(LocalDateTime.of(2024, 12, 25, 9, 15)); // December 25, 2024
            complaint2.setAssignedTo(emp1);
            complaintRepository.save(complaint2);

            // Complaint 3 - Anonymous staff complaint
            Complaint complaint3 = new Complaint();
            complaint3.setComplaintId("C2025003");
            complaint3.setSubject("Discrimination and Harassment by Manager Sarah Johnson");
            complaint3.setDescription(
                    "I am filing this complaint regarding serious workplace harassment and discrimination by my direct manager, Sarah Johnson, in the Marketing Department. Over the past three months, she has: 1) Made inappropriate comments about my ethnicity during team meetings, 2) Consistently assigned me extra work while giving lighter loads to other team members, 3) Excluded me from important project meetings and decision-making processes, 4) Made derogatory remarks about my accent in front of clients. This behavior is creating a hostile work environment and affecting my mental health and job performance. I have witnesses to these incidents and have documented dates and times. I request immediate investigation and action to stop this harassment. I fear retaliation if my identity is revealed, hence filing anonymously.");
            complaint3.setCategory(Complaint.Category.STAFF);
            complaint3.setPriority(Complaint.Priority.URGENT);
            complaint3.setSubmissionType(Complaint.SubmissionType.ANONYMOUS);
            complaint3.setStatus(Complaint.Status.IN_PROGRESS);
            complaint3.setCreatedAt(LocalDateTime.of(2024, 12, 22, 16, 45)); // December 22, 2024
            complaint3.setAssignedTo(emp2);
            complaintRepository.save(complaint3);

            // Complaint 4 - Public technical complaint (resolved)
            Complaint complaint4 = new Complaint();
            complaint4.setComplaintId("C2025004");
            complaint4.setSubject("Mobile App Crashes and Data Loss Issue");
            complaint4.setDescription(
                    "Hello Support Team, I am experiencing critical issues with your mobile application (version 3.2.1) on my iPhone 14 Pro. The app crashes every time I try to: 1) Upload photos to my profile, 2) Access the payment history section, 3) Use the search function for more than 30 seconds. Additionally, I lost all my saved preferences and favorites after the last crash on January 2nd, 2025. This is extremely frustrating as I use the app daily for work purposes. I have tried reinstalling the app, restarting my phone, and clearing the cache, but the issues persist. Please provide a fix or rollback to the previous stable version. My user ID is SG2024001 and device iOS version is 17.2.1.");
            complaint4.setCategory(Complaint.Category.TECHNICAL);
            complaint4.setPriority(Complaint.Priority.HIGH);
            complaint4.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint4.setUser(user);
            complaint4.setStatus(Complaint.Status.RESOLVED);
            complaint4.setAssignedTo(emp1);
            complaint4.setCreatedAt(java.time.LocalDateTime.now().minusDays(5));
            complaint4.setResolvedAt(java.time.LocalDateTime.now().minusDays(2)); // 3 days resolution
            complaintRepository.save(complaint4);

            // Complaint 5 - Anonymous facility complaint
            Complaint complaint5 = new Complaint();
            complaint5.setComplaintId("C2025005");
            complaint5.setSubject("Unsafe Working Conditions - Broken Equipment and Poor Ventilation");
            complaint5.setDescription(
                    "I am reporting serious safety hazards in the manufacturing facility, Building C, Floor 2. The following issues pose immediate risks to employee safety: 1) Three conveyor belts have exposed wiring and sparking electrical connections, 2) The ventilation system has been malfunctioning for two weeks, causing poor air quality and chemical fumes to accumulate, 3) Emergency exit doors are blocked by equipment and storage boxes, 4) Safety equipment like fire extinguishers have expired dates (last serviced in 2022), 5) The floor is constantly wet due to a leak in the ceiling, creating slip hazards. Several employees have reported headaches and respiratory issues. We fear for our safety and request immediate action before someone gets seriously injured. OSHA regulations are being violated and this needs urgent attention from management.");
            complaint5.setCategory(Complaint.Category.FACILITY);
            complaint5.setPriority(Complaint.Priority.URGENT);
            complaint5.setSubmissionType(Complaint.SubmissionType.ANONYMOUS);
            complaint5.setStatus(Complaint.Status.NEW);
            complaintRepository.save(complaint5);

            // Complaint 6 - Public complaint from Anonymous User (closed)
            Complaint complaint6 = new Complaint();
            complaint6.setComplaintId("C2025006");
            complaint6.setSubject("Delayed Delivery and Poor Customer Communication");
            complaint6.setDescription(
                    "Dear Customer Service, I am extremely disappointed with the service I received regarding my recent order #ORD456789. I placed an order for a laptop computer on December 15th, 2024, with a promised delivery date of December 22nd, 2024. However, the product was not delivered until January 5th, 2025 - two weeks late! During this time: 1) I received no communication about the delay until I called on December 23rd, 2) Customer service representatives gave me different information each time I called, 3) I was promised expedited shipping as compensation but still received standard delivery, 4) The tracking information was never updated properly. This delay caused significant inconvenience as the laptop was needed for an important work presentation. While I eventually received the product, the lack of communication and false promises are unacceptable. I expect better service standards and proper compensation for this poor experience.");
            complaint6.setCategory(Complaint.Category.SERVICE);
            complaint6.setPriority(Complaint.Priority.MEDIUM);
            complaint6.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint6.setUser(user1);
            complaint6.setStatus(Complaint.Status.CLOSED);
            complaint6.setCreatedAt(java.time.LocalDateTime.now().minusDays(4));
            complaint6.setResolvedAt(java.time.LocalDateTime.now().minusDays(1)); // 3 days resolution
            complaintRepository.save(complaint6);

            // Complaint 7 - Recent anonymous urgent complaint
            Complaint complaint7 = new Complaint();
            complaint7.setComplaintId("C2025007");
            complaint7.setSubject("Personal Data Breach - Unauthorized Access to Medical Records");
            complaint7.setDescription(
                    "URGENT: I am reporting a serious data privacy breach that occurred on January 4th, 2025. I discovered that my personal medical records, including sensitive health information, prescription details, and insurance data, were accessed without my authorization. Here's what happened: 1) I received a call from an unknown insurance company who had detailed knowledge of my recent medical procedures and medications, 2) They mentioned specific dates of my doctor visits and test results that should be confidential, 3) When I asked how they obtained this information, they claimed it was provided by your organization, 4) I have never given consent for sharing my medical data with any third parties. This is a serious violation of HIPAA regulations and my privacy rights. I demand: immediate investigation into this breach, identification of how my data was compromised, assurance that my information will be secured, and compensation for this violation. I am considering legal action if this matter is not resolved promptly. This breach has caused me significant distress and concern about the security of my personal information.");
            complaint7.setCategory(Complaint.Category.OTHER);
            complaint7.setPriority(Complaint.Priority.URGENT);
            complaint7.setSubmissionType(Complaint.SubmissionType.ANONYMOUS);
            complaint7.setStatus(Complaint.Status.NEW);
            complaint7.setCreatedAt(LocalDateTime.of(2025, 1, 5, 11, 20)); // January 5, 2025
            complaintRepository.save(complaint7);

            // Complaint 8 - Public billing complaint (escalated)
            Complaint complaint8 = new Complaint();
            complaint8.setComplaintId("C2025008");
            complaint8.setSubject("Fraudulent Subscription Charges - $89.99 Monthly for Cancelled Service");
            complaint8.setDescription(
                    "This is my FOURTH complaint regarding ongoing fraudulent charges for a service I cancelled six months ago. Despite multiple complaints (Reference numbers: C2024089, C2024156, C2024203), your billing department continues to charge my credit card $89.99 monthly for 'Premium Digital Services' that I explicitly cancelled on July 15th, 2024. Here's the timeline: July 15, 2024 - Cancelled service via phone (Confirmation #CNF789456), August-January 2025 - Continued monthly charges of $89.99, Multiple calls to customer service with promises to 'fix the issue', Three previous complaints filed with no resolution, Total fraudulent charges: $537.94 (6 months × $89.99). I have: 1) Cancellation confirmation email and reference number, 2) Bank statements showing unauthorized charges, 3) Call logs of my attempts to resolve this, 4) Screenshots of my account showing 'Service Cancelled' status. This is clearly fraudulent billing and I am considering filing complaints with the Better Business Bureau and my state's Attorney General office. I demand immediate cessation of all charges, full refund of $537.94, and written confirmation that this will not happen again. Account: SG789456123, Card ending: 4567.");
            complaint8.setCategory(Complaint.Category.BILLING);
            complaint8.setPriority(Complaint.Priority.URGENT);
            complaint8.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint8.setUser(user);
            complaint8.setStatus(Complaint.Status.ESCALATED);
            complaint8.setCreatedAt(LocalDateTime.of(2025, 1, 3, 8, 45)); // January 3, 2025
            complaint8.setAssignedTo(emp2);
            complaintRepository.save(complaint8);

            // Complaint 9 - OLD complaint for auto-escalation testing (10 days old)
            Complaint complaint9 = new Complaint();
            complaint9.setComplaintId("C2024999");
            complaint9.setSubject("Defective Washing Machine - Water Damage to Property");
            complaint9.setDescription(
                    "I purchased a washing machine (Model WM-5000X, Serial: WM789456123) from your store on December 20th, 2024. After just 8 days of normal use, the machine malfunctioned catastrophically on December 28th, 2024, causing significant water damage to my home. The incident occurred when: 1) The machine's water inlet valve failed during a normal wash cycle, 2) Water flooded my laundry room, kitchen, and living room, 3) The machine continued pumping water for over 2 hours before I discovered the flood, 4) Damage includes: hardwood floors ($3,200), kitchen cabinets ($1,800), personal belongings ($900), carpet replacement ($1,100). Total property damage: $7,000. I immediately contacted your customer service on December 28th but have received no satisfactory response. Your technician confirmed the valve defect was due to manufacturing fault, not user error. I have: 1) Purchase receipt and warranty documentation, 2) Photos and videos of the flood damage, 3) Insurance adjuster's report confirming cause, 4) Repair estimates from contractors. I demand: immediate replacement of the defective machine, full compensation for property damage ($7,000), coverage of temporary accommodation costs ($800 for 3 nights in hotel), and expedited resolution as this has caused significant hardship to my family. This defective product has caused financial and emotional distress that could have been avoided with proper quality control.");
            complaint9.setCategory(Complaint.Category.OTHER);
            complaint9.setPriority(Complaint.Priority.URGENT);
            complaint9.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint9.setUser(user);
            complaint9.setStatus(Complaint.Status.NEW);
            // Set creation date to 10 days ago
            complaint9.setCreatedAt(java.time.LocalDateTime.now().minusDays(10));
            complaintRepository.save(complaint9);

            // Complaint 10 - 5 days old (not yet eligible for escalation)
            Complaint complaint10 = new Complaint();
            complaint10.setComplaintId("C2025010");
            complaint10.setSubject("Internet Service Outage - Business Impact");
            complaint10.setDescription(
                    "Our business internet service has been experiencing frequent outages over the past week, causing significant disruption to our operations. The outages occur 3-4 times daily, lasting 15-30 minutes each time. This is affecting our ability to serve customers, process online orders, and maintain communication with suppliers. We've lost approximately $2,500 in revenue due to these disruptions. Our business relies heavily on stable internet connectivity for: 1) Point-of-sale systems, 2) Inventory management, 3) Customer communications, 4) Online order processing. We need immediate resolution and compensation for lost business. Account: BIZ-789456, Service Address: 123 Business Park Drive.");
            complaint10.setCategory(Complaint.Category.TECHNICAL);
            complaint10.setPriority(Complaint.Priority.URGENT);
            complaint10.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint10.setUser(user1);
            complaint10.setStatus(Complaint.Status.NEW);
            complaint10.setCreatedAt(java.time.LocalDateTime.now().minusDays(5));
            complaintRepository.save(complaint10);

            // Complaint 11 - 8 days old (eligible for escalation)
            Complaint complaint11 = new Complaint();
            complaint11.setComplaintId("C2024998");
            complaint11.setSubject("Medication Delivery Error - Wrong Prescription Received");
            complaint11.setDescription(
                    "URGENT: I received the wrong prescription medication in my delivery on December 30th, 2024. I ordered my regular blood pressure medication (Lisinopril 10mg) but received diabetes medication (Metformin 500mg) instead. This is a serious safety issue that could have caused severe health complications if I had taken the wrong medication. I immediately contacted customer service but have not received adequate response or resolution. This error indicates serious problems in your pharmacy fulfillment process. I need: 1) Immediate delivery of correct medication, 2) Investigation into how this error occurred, 3) Assurance this won't happen again, 4) Compensation for the stress and potential health risk. Patient ID: PAT789456, Order #: RX2024-5678.");
            complaint11.setCategory(Complaint.Category.OTHER);
            complaint11.setPriority(Complaint.Priority.URGENT);
            complaint11.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint11.setUser(user);
            complaint11.setStatus(Complaint.Status.UNDER_REVIEW);
            complaint11.setAssignedTo(emp1);
            complaint11.setCreatedAt(java.time.LocalDateTime.now().minusDays(8));
            complaintRepository.save(complaint11);

            // Complaint 12 - 12 days old (definitely eligible for escalation)
            Complaint complaint12 = new Complaint();
            complaint12.setComplaintId("C2024997");
            complaint12.setSubject("Insurance Claim Denial - Lack of Proper Investigation");
            complaint12.setDescription(
                    "My insurance claim for vehicle damage (Claim #: INS-2024-9876) was denied without proper investigation. The denial letter cited 'insufficient evidence' but your adjuster never contacted me, never inspected the vehicle, and never requested additional documentation. The accident occurred on December 15th, 2024, when a tree fell on my car during a storm. I have: 1) Police report documenting the incident, 2) Weather service reports confirming severe storm conditions, 3) Photos of the damaged vehicle and fallen tree, 4) Repair estimates totaling $8,500. This denial appears to be an attempt to avoid paying a legitimate claim. I demand proper investigation, claim approval, and compensation for the delays. Policy #: POL789456123.");
            complaint12.setCategory(Complaint.Category.BILLING);
            complaint12.setPriority(Complaint.Priority.HIGH);
            complaint12.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint12.setUser(user1);
            complaint12.setStatus(Complaint.Status.IN_PROGRESS);
            complaint12.setAssignedTo(emp2);
            complaint12.setCreatedAt(java.time.LocalDateTime.now().minusDays(12));
            complaintRepository.save(complaint12);

            // Complaint 13 - 15 days old (very old, definitely needs escalation)
            Complaint complaint13 = new Complaint();
            complaint13.setComplaintId("C2024996");
            complaint13.setSubject("Construction Noise Violation - Residential Area Disruption");
            complaint13.setDescription(
                    "The construction project at 456 Oak Street has been violating noise ordinances for over two weeks, causing significant disruption to our residential neighborhood. Construction begins at 6:00 AM (ordinance allows 7:00 AM) and continues until 9:00 PM (ordinance requires stop at 6:00 PM). The noise levels exceed 85 decibels, well above the 65-decibel residential limit. This is affecting: 1) Children's sleep schedules, 2) Remote work productivity, 3) Elderly residents' health and well-being, 4) Property values in the area. Multiple neighbors have complained but no action has been taken. We have documented evidence including: decibel readings, video recordings, timestamps of violations. We demand immediate enforcement of noise ordinances, restricted construction hours, and compensation for the ongoing disruption. Permit #: CONST-2024-789.");
            complaint13.setCategory(Complaint.Category.OTHER);
            complaint13.setPriority(Complaint.Priority.HIGH);
            complaint13.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint13.setUser(user);
            complaint13.setStatus(Complaint.Status.NEW);
            complaint13.setCreatedAt(java.time.LocalDateTime.now().minusDays(15));
            complaintRepository.save(complaint13);

            // Additional Previous Complaints for Better Historical Data

            // Previous Complaint 14 - 20 days old (Very old, needs immediate escalation)
            Complaint complaint14 = new Complaint();
            complaint14.setComplaintId("C2024995");
            complaint14.setSubject("Recurring Billing Error - Overcharged for 3 Months");
            complaint14.setDescription(
                    "I have been overcharged for the past 3 months (October, November, December 2024). My plan is supposed to be $49.99/month but I've been charged $79.99 each month. I've called customer service multiple times and was promised the issue would be fixed, but it keeps happening. I need a refund of $90 ($30 x 3 months) and assurance this won't happen again. Account number: ACC-567890. This is causing financial strain and I'm considering switching providers if not resolved immediately.");
            complaint14.setCategory(Complaint.Category.BILLING);
            complaint14.setPriority(Complaint.Priority.URGENT);
            complaint14.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint14.setUser(user);
            complaint14.setStatus(Complaint.Status.NEW);
            complaint14.setCreatedAt(java.time.LocalDateTime.now().minusDays(20));
            complaint14.setAssignedTo(emp1);
            complaintRepository.save(complaint14);

            // Previous Complaint 15 - 18 days old (Old technical issue)
            Complaint complaint15 = new Complaint();
            complaint15.setComplaintId("C2024994");
            complaint15.setSubject("Website Security Vulnerability - Personal Data Exposed");
            complaint15.setDescription(
                    "I discovered a serious security flaw on your website. When I log into my account, I can see other customers' personal information including names, addresses, and partial credit card numbers by manipulating the URL parameters. This is a major privacy breach and GDPR violation. I have screenshots as evidence but won't share them publicly for obvious security reasons. This needs immediate attention from your IT security team. I'm a cybersecurity professional and this is one of the worst vulnerabilities I've seen. Please contact me directly to discuss the technical details.");
            complaint15.setCategory(Complaint.Category.TECHNICAL);
            complaint15.setPriority(Complaint.Priority.URGENT);
            complaint15.setSubmissionType(Complaint.SubmissionType.ANONYMOUS);
            complaint15.setStatus(Complaint.Status.UNDER_REVIEW);
            complaint15.setCreatedAt(java.time.LocalDateTime.now().minusDays(18));
            complaint15.setAssignedTo(emp2);
            complaintRepository.save(complaint15);

            // Previous Complaint 16 - 25 days old (Very old service complaint)
            Complaint complaint16 = new Complaint();
            complaint16.setComplaintId("C2024993");
            complaint16.setSubject("Discrimination by Staff - Refused Service Based on Appearance");
            complaint16.setDescription(
                    "On December 15, 2024, I visited your downtown location and was refused service by an employee named Marcus. He made discriminatory comments about my appearance and told me to 'go somewhere else' when I asked for help with my account. Other customers were being served normally, but I was ignored and treated rudely. This is clear discrimination and violates civil rights laws. I have witnesses who saw the incident. I demand an apology, disciplinary action against the employee, and sensitivity training for all staff. This behavior is unacceptable and illegal.");
            complaint16.setCategory(Complaint.Category.STAFF);
            complaint16.setPriority(Complaint.Priority.HIGH);
            complaint16.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint16.setUser(user1);
            complaint16.setStatus(Complaint.Status.IN_PROGRESS);
            complaint16.setCreatedAt(java.time.LocalDateTime.now().minusDays(25));
            complaint16.setAssignedTo(emp1);
            complaintRepository.save(complaint16);

            // Previous Complaint 17 - 30 days old (Month-old facility issue)
            Complaint complaint17 = new Complaint();
            complaint17.setComplaintId("C2024992");
            complaint17.setSubject("Hazardous Chemical Spill - Environmental Safety Concern");
            complaint17.setDescription(
                    "There was a chemical spill in the parking lot of your facility on December 10, 2024. The spill was not properly cleaned up and has been seeping into the storm drains for weeks. The area still smells strongly of chemicals and there are dead plants and discolored pavement. This is an environmental hazard that could contaminate groundwater and harm wildlife. I've contacted the EPA but wanted to give you a chance to address this first. Immediate professional hazmat cleanup is required. Location: North parking lot, near the loading dock. This is a serious environmental violation.");
            complaint17.setCategory(Complaint.Category.FACILITY);
            complaint17.setPriority(Complaint.Priority.URGENT);
            complaint17.setSubmissionType(Complaint.SubmissionType.ANONYMOUS);
            complaint17.setStatus(Complaint.Status.NEW);
            complaint17.setCreatedAt(java.time.LocalDateTime.now().minusDays(30));
            complaintRepository.save(complaint17);

            // Previous Complaint 18 - 22 days old (Old product quality issue)
            Complaint complaint18 = new Complaint();
            complaint18.setComplaintId("C2024991");
            complaint18.setSubject("Defective Product Batch - Multiple Items Failing");
            complaint18.setDescription(
                    "I purchased 5 units of your Model ABC-789 devices in November 2024 for my small business. All 5 units have failed within 2 weeks of use with the same error: 'System Overheating - Shutdown Required'. This suggests a defective batch or design flaw. The devices are critical for my business operations and the failures have caused significant downtime and lost revenue. I need immediate replacements and compensation for business losses. Order numbers: ORD-111222, ORD-111223, ORD-111224, ORD-111225, ORD-111226. This appears to be a widespread quality control issue that needs investigation.");
            complaint18.setCategory(Complaint.Category.OTHER);
            complaint18.setPriority(Complaint.Priority.HIGH);
            complaint18.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint18.setUser(user);
            complaint18.setStatus(Complaint.Status.UNDER_REVIEW);
            complaint18.setCreatedAt(java.time.LocalDateTime.now().minusDays(22));
            complaint18.setAssignedTo(emp2);
            complaintRepository.save(complaint18);

            // Previous Complaint 19 - 14 days old (2 weeks old, eligible for escalation)
            Complaint complaint19 = new Complaint();
            complaint19.setComplaintId("C2024990");
            complaint19.setSubject("Identity Theft - Account Compromised by Fraudulent Activity");
            complaint19.setDescription(
                    "My account was compromised on December 25, 2024. Someone gained unauthorized access and changed my password, email address, and billing information. They made several purchases totaling $1,247.89 before I noticed and called customer service. Your security team said they would investigate, but I haven't heard back in 2 weeks. Meanwhile, I'm being held responsible for the fraudulent charges. I've filed a police report (Case #2024-789456) and contacted the FTC. I need immediate account recovery, removal of all fraudulent charges, and a security audit to prevent this from happening to other customers.");
            complaint19.setCategory(Complaint.Category.TECHNICAL);
            complaint19.setPriority(Complaint.Priority.URGENT);
            complaint19.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            complaint19.setUser(user1);
            complaint19.setStatus(Complaint.Status.IN_PROGRESS);
            complaint19.setCreatedAt(java.time.LocalDateTime.now().minusDays(14));
            complaint19.setAssignedTo(emp1);
            complaintRepository.save(complaint19);

            // Previous Complaint 20 - 16 days old (Over 2 weeks old)
            Complaint complaint20 = new Complaint();
            complaint20.setComplaintId("C2024989");
            complaint20.setSubject("False Advertising - Product Doesn't Match Description");
            complaint20.setDescription(
                    "Your website advertised a laptop with '16GB RAM, 1TB SSD, and 8-hour battery life' for $899. When I received the product, it only has 8GB RAM, 512GB SSD, and the battery lasts 3 hours maximum. This is clear false advertising and consumer fraud. I have screenshots of the original listing and the actual product specifications. I want either the correct product as advertised or a full refund plus compensation for my time and inconvenience. Order number: ORD-998877. This deceptive practice is illegal and I'm considering filing a complaint with the Better Business Bureau.");
            complaint20.setCategory(Complaint.Category.SERVICE);
            complaint20.setPriority(Complaint.Priority.HIGH);
            complaint20.setSubmissionType(Complaint.SubmissionType.ANONYMOUS);
            complaint20.setStatus(Complaint.Status.NEW);
            complaint20.setCreatedAt(java.time.LocalDateTime.now().minusDays(16));
            complaintRepository.save(complaint20);

            System.out.println(
                    "DataLoader: Added 7 additional previous complaints (14-30 days old) for comprehensive auto-escalation testing");

            // Add more resolved complaints to achieve 3-day average resolution time

            // Resolved Complaint 1 - Resolved in 1 day
            Complaint resolvedComplaint1 = new Complaint();
            resolvedComplaint1.setComplaintId("R2025001");
            resolvedComplaint1.setSubject("Quick Fix - Password Reset Issue");
            resolvedComplaint1.setDescription(
                    "Unable to reset my password using the automated system. The reset email is not being received.");
            resolvedComplaint1.setCategory(Complaint.Category.TECHNICAL);
            resolvedComplaint1.setPriority(Complaint.Priority.MEDIUM);
            resolvedComplaint1.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            resolvedComplaint1.setUser(user);
            resolvedComplaint1.setStatus(Complaint.Status.RESOLVED);
            resolvedComplaint1.setAssignedTo(emp1);
            resolvedComplaint1.setCreatedAt(java.time.LocalDateTime.now().minusDays(8));
            resolvedComplaint1.setResolvedAt(java.time.LocalDateTime.now().minusDays(7)); // 1 day resolution
            complaintRepository.save(resolvedComplaint1);

            // Resolved Complaint 2 - Resolved in 3 days
            Complaint resolvedComplaint2 = new Complaint();
            resolvedComplaint2.setComplaintId("R2025002");
            resolvedComplaint2.setSubject("Billing Inquiry - Duplicate Charge");
            resolvedComplaint2.setDescription(
                    "I was charged twice for the same service on my December bill. Need refund for duplicate charge of $45.99.");
            resolvedComplaint2.setCategory(Complaint.Category.BILLING);
            resolvedComplaint2.setPriority(Complaint.Priority.HIGH);
            resolvedComplaint2.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            resolvedComplaint2.setUser(user1);
            resolvedComplaint2.setStatus(Complaint.Status.RESOLVED);
            resolvedComplaint2.setAssignedTo(emp2);
            resolvedComplaint2.setCreatedAt(java.time.LocalDateTime.now().minusDays(10));
            resolvedComplaint2.setResolvedAt(java.time.LocalDateTime.now().minusDays(7)); // 3 days resolution
            complaintRepository.save(resolvedComplaint2);

            // Resolved Complaint 3 - Resolved in 2 days
            Complaint resolvedComplaint3 = new Complaint();
            resolvedComplaint3.setComplaintId("R2025003");
            resolvedComplaint3.setSubject("Service Appointment Rescheduling");
            resolvedComplaint3.setDescription(
                    "Need to reschedule my service appointment due to emergency. Original appointment was January 10th.");
            resolvedComplaint3.setCategory(Complaint.Category.SERVICE);
            resolvedComplaint3.setPriority(Complaint.Priority.LOW);
            resolvedComplaint3.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            resolvedComplaint3.setUser(user);
            resolvedComplaint3.setStatus(Complaint.Status.RESOLVED);
            resolvedComplaint3.setAssignedTo(emp1);
            resolvedComplaint3.setCreatedAt(java.time.LocalDateTime.now().minusDays(6));
            resolvedComplaint3.setResolvedAt(java.time.LocalDateTime.now().minusDays(4)); // 2 days resolution
            complaintRepository.save(resolvedComplaint3);

            // Resolved Complaint 4 - Resolved in 4 days
            Complaint resolvedComplaint4 = new Complaint();
            resolvedComplaint4.setComplaintId("R2025004");
            resolvedComplaint4.setSubject("Product Return Request");
            resolvedComplaint4.setDescription(
                    "Product received was damaged during shipping. Need return authorization and replacement.");
            resolvedComplaint4.setCategory(Complaint.Category.OTHER);
            resolvedComplaint4.setPriority(Complaint.Priority.MEDIUM);
            resolvedComplaint4.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            resolvedComplaint4.setUser(user1);
            resolvedComplaint4.setStatus(Complaint.Status.RESOLVED);
            resolvedComplaint4.setAssignedTo(emp2);
            resolvedComplaint4.setCreatedAt(java.time.LocalDateTime.now().minusDays(9));
            resolvedComplaint4.setResolvedAt(java.time.LocalDateTime.now().minusDays(5)); // 4 days resolution
            complaintRepository.save(resolvedComplaint4);

            // Resolved Complaint 5 - Resolved in 5 days
            Complaint resolvedComplaint5 = new Complaint();
            resolvedComplaint5.setComplaintId("R2025005");
            resolvedComplaint5.setSubject("Account Access Issue");
            resolvedComplaint5.setDescription(
                    "Unable to access my account after recent system update. Getting error message 'Account Locked'.");
            resolvedComplaint5.setCategory(Complaint.Category.TECHNICAL);
            resolvedComplaint5.setPriority(Complaint.Priority.HIGH);
            resolvedComplaint5.setSubmissionType(Complaint.SubmissionType.PUBLIC);
            resolvedComplaint5.setUser(user);
            resolvedComplaint5.setStatus(Complaint.Status.RESOLVED);
            resolvedComplaint5.setAssignedTo(emp1);
            resolvedComplaint5.setCreatedAt(java.time.LocalDateTime.now().minusDays(12));
            resolvedComplaint5.setResolvedAt(java.time.LocalDateTime.now().minusDays(7)); // 5 days resolution
            complaintRepository.save(resolvedComplaint5);

            // Update the existing resolved complaint (C2025004) to have proper resolution
            // date
            // This will be handled by setting resolvedAt after creation

            System.out.println("DataLoader: Realistic sample complaints created successfully!");
            System.out.println("DataLoader: Created 25 complaints with realistic content and varied timestamps");
            System.out.println("DataLoader: - 6 ANONYMOUS complaints (privacy protected)");
            System.out.println("DataLoader: - 19 PUBLIC complaints (trackable by users)");
            System.out.println("DataLoader: - 7 RESOLVED/CLOSED complaints with 3-day average resolution time");
            System.out.println("DataLoader: - Multiple OLD complaints for auto-escalation testing:");
            System.out.println("DataLoader:   * 5 days old (not eligible yet)");
            System.out.println("DataLoader:   * 8 days old (eligible for escalation)");
            System.out.println("DataLoader:   * 10 days old (eligible for escalation)");
            System.out.println("DataLoader:   * 12 days old (eligible for escalation)");
            System.out.println("DataLoader:   * 14 days old (eligible for escalation)");
            System.out.println("DataLoader:   * 15 days old (definitely needs escalation)");
            System.out.println("DataLoader:   * 16 days old (definitely needs escalation)");
            System.out.println("DataLoader:   * 18 days old (definitely needs escalation)");
            System.out.println("DataLoader:   * 20 days old (definitely needs escalation)");
            System.out.println("DataLoader:   * 22 days old (definitely needs escalation)");
            System.out.println("DataLoader:   * 25 days old (definitely needs escalation)");
            System.out.println("DataLoader:   * 30 days old (definitely needs escalation)");
            System.out.println(
                    "DataLoader: - Various statuses: NEW, UNDER_REVIEW, IN_PROGRESS, RESOLVED, CLOSED, ESCALATED");
            System.out.println("DataLoader: - Complaints assigned to emp1 and emp2 for testing");
        } else {
            System.out.println("DataLoader: Sample complaints already exist, skipping creation.");
        }

        // Create a test personal note for emp2
        try {
            User admin = userRepository.findByUsername("admin").orElse(null);
            User emp1 = userRepository.findByUsername("emp1").orElse(null);
            User emp2 = userRepository.findByUsername("emp2").orElse(null);
            User emp3 = userRepository.findByUsername("emp3").orElse(null);

            if (admin != null && emp1 != null && emp2 != null && emp3 != null) {
                System.out.println("DataLoader: Creating test personal notes...");

                // We'll create the notes through the PersonalNoteService in a separate method
                // For now, just log that we have the users
                System.out.println("DataLoader: Admin and employees found, ready for personal notes");
            }
        } catch (Exception e) {
            System.out.println("DataLoader: Error preparing personal notes: " + e.getMessage());
        }

        System.out.println("DataLoader: Data initialization completed.");
    }
}