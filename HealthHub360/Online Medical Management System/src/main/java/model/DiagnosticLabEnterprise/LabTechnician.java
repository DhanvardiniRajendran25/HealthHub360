/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterprise;

import model.Person;

/**
 *
 * @author AnjanaSruthiR
 */

public class LabTechnician extends Person {
    private String certification; // E.g., Certified Pathologist, Radiologist
    private String workShift; // Morning, Evening, Night
    private String specialization; // Hematology, Radiology, etc.
    private int yearsOfExperience;

    // Constructor
    public LabTechnician(int id, String name, String contactNumber, String email, String address,
                         String certification, String workShift, String specialization, int yearsOfExperience) {
        super(id, name, contactNumber, email, address);
        this.certification = certification;
        this.workShift = workShift;
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
    }

    // Getters and Setters
    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getWorkShift() {
        return workShift;
    }

    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    @Override
    public String toString() {
        return super.toString() + " LabTechnician{certification='" + certification + "', workShift='" + workShift + 
               "', specialization='" + specialization + "', yearsOfExperience=" + yearsOfExperience + "}";
    }
}

