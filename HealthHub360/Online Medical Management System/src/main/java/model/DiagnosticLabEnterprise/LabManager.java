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

public class LabManager extends Person {
    private String department; // E.g., Pathology, Radiology
    private int yearsOfExperience;

    // Constructor
    public LabManager(int id, String name, String contactNumber, String email, String address, 
                      String department, int yearsOfExperience) {
        super(id, name, contactNumber, email, address);
        this.department = department;
        this.yearsOfExperience = yearsOfExperience;
    }

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    @Override
    public String toString() {
        return super.toString() + " LabManager{department='" + department + "', yearsOfExperience=" + yearsOfExperience + "}";
    }
}
