/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;
import modelHospitalEnterprise.Appointment;
import modelHospitalEnterprise.MedicalRecord;

/**
 *
 * @author rdhan
 */
public class Patient extends Person {
    
    private String insuranceNumber;
    private String dateOfBirth;
    
    private List<MedicalRecord> medicalHistory;
    private List<Appointment> appointments;

    // Constructor
    public Patient(int personId, String insuranceNumber, String dateOfBirth, String name, String email, String phoneNumber) {
    super(personId, "", "", "", ""); // Create Person but leave name, email, etc., empty
    this.insuranceNumber = insuranceNumber;
    this.dateOfBirth = dateOfBirth;
    
    this.medicalHistory = new ArrayList<>();
    this.appointments = new ArrayList<>();
}

    public List<MedicalRecord> getMedicalHistory() {
    if (medicalHistory == null) {
        medicalHistory = new ArrayList<>(); // Initialize if null
    }
    return medicalHistory;
}

    public void setMedicalHistory(List<MedicalRecord> medicalHistory) {
    if (medicalHistory == null) {
        this.medicalHistory = new ArrayList<>(); // Avoid null assignment
    } else {
        this.medicalHistory = medicalHistory;
    }
}


    public void addMedicalRecord(MedicalRecord record) {
        this.medicalHistory.add(record);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }
    
    // Getters and Setters
    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + getId() +  // Get id from Person class
                ", name='" + getName() + '\'' +
                ", contactNumber='" + getContactNumber() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", insuranceNumber='" + insuranceNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }
    
    
    
}
