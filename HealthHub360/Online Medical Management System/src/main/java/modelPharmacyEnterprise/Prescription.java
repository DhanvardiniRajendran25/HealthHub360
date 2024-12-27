/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterprise;

import java.sql.Date;

/**
 *
 * @author keerthichandrakanth
 */
public class Prescription {
     private int id;
    private int patientId;
    private int doctorId;
    private int pharmacyManagerId;
    private Date dateRequested;
    private String status;
    private String patientName; // Store patient name
    private String doctorName;  // Store doctor name
    private String assignedPharmacistName; 
    private String paymentStatus;

    // Constructor
       // Constructor (including new fields)
    public Prescription(int prescriptionId, String patientName, String doctorName, 
                    int pharmacyManagerId, Date dateRequested, String status) {
    this.id = prescriptionId;
    this.patientName = patientName;    // Set patient name
    this.doctorName = doctorName;      // Set doctor name
    this.pharmacyManagerId = pharmacyManagerId;
    this.dateRequested = dateRequested;
    this.status = status;
}
    
       // Constructor for new prescription creation (excludes names)
    public Prescription(int patientId, int doctorId, int pharmacyManagerId, Date dateRequested, String status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.pharmacyManagerId = pharmacyManagerId;
        this.dateRequested = dateRequested;
        this.status = status;
    }

    
        public Prescription(int patientId, int doctorId, int pharmacyManagerId, Date dateRequested, String status,String paymentStatus) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.pharmacyManagerId = pharmacyManagerId;
        this.dateRequested = dateRequested;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
 
    
     public String getAssignedPharmacistName() {
        return assignedPharmacistName;
    }

    public void setAssignedPharmacistName(String assignedPharmacistName) {
        this.assignedPharmacistName = assignedPharmacistName;
    }
    
    
    public Prescription(int id, String status) {
    this.id = id;
    this.status = status;
}
    
     public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPharmacyManagerId() {
        return pharmacyManagerId;
    }

    public void setPharmacyManagerId(int pharmacyManagerId) {
        this.pharmacyManagerId = pharmacyManagerId;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
