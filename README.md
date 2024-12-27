# HealthHub360

### Overview  
The **HealthHub360** is an innovative healthcare platform designed to centralize and streamline various medical services for patients, doctors, staff, and administrators. By providing secure, role-based access to essential functionalities like appointment scheduling, medical record management, lab reporting, billing, and insurance claims, HealthHub360 simplifies the complex processes of healthcare management. This system is designed to enhance operational efficiency, ensure data security, and improve patient satisfaction on a global scale.  

---

### 🛠️ Technologies Used  
- **Frontend**: Swing UI  
- **Backend**: Java 
- **Database**: SQL, SSMS  
- **Version Control**: GitHub

The **HealthHub360** is a Java-based application developed using **Java Swing**, integrated with **DB4OUtil** for database management. It features secure **Password Encryption** for user authentication. Designed with a modular architecture, it supports role-based access for patients, doctors, and administrators. The system streamlines healthcare processes like appointment scheduling, medical history tracking, lab management, and billing while ensuring scalability and robust security. The system features an **inline chat support module** with pre-built responses to common queries, providing quick assistance to users without requiring real-time communication.

---

### High-Level Component Diagram

![Wednesday_Group_30_Final_Project - High_level_component_diagram](https://github.com/user-attachments/assets/2846f71b-abbd-4ed2-ac53-c6a7da5df63d)

---

### 🩺 **Problem Statement**  
Healthcare services often face challenges in streamlining operations, managing appointments, tracking medical history, and facilitating secure transactions. This application aims to simplify healthcare access by providing dependable, adaptable, and globally accessible functionalities, thereby enhancing operational efficiency across medical facilities.

---

### 💡 **Proposed Solution**  
The **Online Medical Management System** is a centralized platform designed for healthcare management. It ensures secure role-based access for stakeholders, enabling:  
- **Patient Registration and Appointment Scheduling**  
- **Medical Record Updates and Billing**  
- **Doctor Consultations and Prescription Management**  
- **Lab Testing and Reporting**  
- **Insurance Claim Processing**  

This system promotes seamless interaction among patients, doctors, staff, and administrators.  

---

### 🏢 **Entities**  

![Wednesday_Group_30_Final_Project - UML_Class_Diagram](https://github.com/user-attachments/assets/b887338d-62ed-4d3f-9a1f-ca1c0b5e5aa9)

#### **Enterprises**  
1. **Hospital Enterprise**  
   - Manages appointments, patient data, and treatments.  
2. **Diagnostic Labs Enterprise**  
   - Conducts medical tests and uploads lab results.  
3. **Pharmacy Enterprise**  
   - Manages medication prescriptions and inventory.  
4. **Insurance Enterprise**  
   - Processes insurance claims and approvals.  

#### **Organizations**  
1. Patient Registration and Support  
2. Doctor Scheduling and Assignments  
3. Diagnostic Lab Operations  
4. Pharmacy Inventory Management  
5. Insurance Verification  
6. Billing and Payments  

#### **Roles**  
1. System Admin  
2. Enterprise Admin  
3. Doctor  
4. Patient  
5. Lab Manager  
6. Lab Technician  
7. Pharmacy Manager  
8. Pharmacist  
9. Insurance Manager  
10. Billing Manager  

---

### ⚙️ **Functional Description**  
#### **1. Login and Role-Based Access**  
- Secure login for administrators, doctors, and patients.  
- Role-based access ensures users can only access relevant features.  

#### **2. Features for Each Role**  
- **System Admin**: Manages users, roles, and enterprises.  
- **Enterprise Admin**: Oversees operations within their enterprise.  
- **Doctor**: Schedules appointments, accesses patient records, prescribes medications.  
- **Patient**: Registers, books appointments, views medical history.  
- **Lab Manager**: Delegates tasks to Lab Technicians, manages results.  
- **Lab Technician**: Processes lab requests and sends results to the manager.  
- **Pharmacy Manager**: Oversees prescriptions, maintains inventory.  
- **Pharmacist**: Fulfills prescriptions, dispenses medication.  
- **Insurance Manager**: Validates claims and processes approvals.  
- **Billing Manager**: Manages invoices and payment processing.  

---

### 📚 **Use Cases**  

#### **1. Hospital Enterprise**  
- Roles: Doctor, Patient  
- Use Cases:  
  - Scheduling appointments.  
  - Accessing and recording appointment history.  

#### **2. Diagnostic Labs Enterprise**  
- Roles: Diagnostic Lab Operations, Lab Manager  
- Use Cases:  
  - Efficient diagnostic test management.  
  - Generating and sharing lab reports.  

#### **3. Pharmacy Enterprise**  
- Roles: Pharmacy Inventory Management, Pharmacist  
- Use Cases:  
  - Managing inventory.  
  - Processing prescriptions.  

#### **4. Insurance Enterprise**  
- Roles: Insurance Manager  
- Use Cases:  
  - Validating insurance claims.  
  - Generating approval reports.  

#### **5. Inline Chat Support**  
- Role: Support Staff  
- Use Case: Addressing real-time patient queries and concerns.  

#### **Cross-Functional Features**  
- **Billing and Payments**: Centralized and secure billing for services, lab tests, and prescriptions.  

---

### 🔗 **Interaction Between Ecosystem Components**  

1. **Doctors and Patients**:  
   - Patients book appointments based on doctor availability.  
   - Doctors manage patient histories.  

2. **Diagnostic Labs and Patients/Doctors**:  
   - Labs process test requests and share reports.  

3. **Pharmacy and Patients**:  
   - Processes prescriptions and ensures medicine availability.  

4. **Insurance and Patients**:  
   - Validates claims and approves services.  

---

### **Java Core Packages:**
1. **`java.awt`** - For GUI elements and event handling.
2. **`java.sql`** - For database connectivity and operations (JDBC).
3. **`java.time`** - For date and time manipulation.
4. **`java.util`** - For collections and utilities like `ArrayList` and `HashMap`.
5. **`java.text.SimpleDateFormat`** - For date formatting.

### **Swing and GUI Packages:**
1. **`javax.swing`** - For creating the graphical user interface, including components like `JPanel`, `JTable`, and `JOptionPane`.

### **Custom Models and Services:**
1. **Diagnostic Lab Enterprise** - Packages for managing lab tests, reports, technicians, and managers.
2. **Insurance Enterprise** - Handles claims, policies, and related services.
3. **Hospital Enterprise** - Manages appointments, prescriptions, and patient records.
4. **Pharmacy Enterprise** - Focuses on medicine inventory, orders, and prescriptions.

### **Custom Utility Packages:**
1. **Chat Support** - For predefined question handling and chat sessions.
2. **`ui.DatabaseUtil`** - For database utility and connection management.

---

### 🔑 Key Achievements  
1. **Centralized Platform**: Developed a unified system integrating hospital, lab, pharmacy, and insurance operations.  
2. **Role-Based Access Control**: Implemented secure access based on user roles, ensuring data privacy and efficient functionality segregation.  
3. **Scalable Architecture**: Designed a scalable backend architecture to handle concurrent user operations seamlessly.  
4. **Pre-Built Responses**: Includes inline chat support with predefined responses to assist patients and staff efficiently.
5. **Comprehensive Medical Record Management**: Enabled easy tracking and retrieval of patient medical histories, lab results, and prescriptions.  
6. **Automation**: Streamlined scheduling, billing, and insurance claim processes, reducing manual workload and errors.  
7. **User-Friendly Interface**: Created an intuitive and responsive UI to enhance user experience for patients and administrators.  
8. **Efficient Pharmacy Operations**: Built features to manage prescription fulfillment and inventory with predictive restocking.  

---

### 📊 Impact  
1. **Improved Healthcare Accessibility**: Enabled patients to schedule appointments, view medical records, and access lab results conveniently online.  
2. **Operational Efficiency**: Automated core hospital processes, reducing time and resource wastage for doctors, administrators, and lab staff.  
3. **Data Security and Privacy**: Leveraged robust authentication mechanisms to protect sensitive medical and personal data.  
4. **Patient Satisfaction**: Enhanced transparency and convenience in accessing healthcare services, improving overall patient satisfaction.  
5. **Global Reach**: Designed a system that can be adapted for diverse healthcare systems worldwide, making quality healthcare services universally accessible.  
6. **Financial Transparency**: Streamlined billing and insurance claims, ensuring clarity and trust among stakeholders.  
7. **Scalability**: Provided a flexible architecture that can support future expansions, including additional features like telemedicine and AI-based diagnostics.

---

### 📬 **Contact**  
For queries or collaboration, please contact [Dhanvardini](mailto:dhanvardini@example.com) 
