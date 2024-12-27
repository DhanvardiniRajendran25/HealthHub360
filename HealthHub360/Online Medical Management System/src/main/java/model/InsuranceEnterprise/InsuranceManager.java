/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.InsuranceEnterprise;

import model.Person;

/**
 *
 * @author AnjanaSruthiR
 */

public class InsuranceManager extends Person {
    private double approvalLimit; // Maximum amount they can approve
    private String department; // E.g., Health Insurance, Life Insurance
    private int claimsProcessed; // Total claims processed

    // Constructor
    public InsuranceManager(int id, String name, String contactNumber, String email, String address,
                            double approvalLimit, String department, int claimsProcessed) {
        super(id, name, contactNumber, email, address);
        this.approvalLimit = approvalLimit;
        this.department = department;
        this.claimsProcessed = claimsProcessed;
    }

    // Getters and Setters
    public double getApprovalLimit() {
        return approvalLimit;
    }

    public void setApprovalLimit(double approvalLimit) {
        this.approvalLimit = approvalLimit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getClaimsProcessed() {
        return claimsProcessed;
    }

    public void setClaimsProcessed(int claimsProcessed) {
        this.claimsProcessed = claimsProcessed;
    }

    @Override
    public String toString() {
        return super.toString() + " InsuranceManager{approvalLimit=" + approvalLimit + ", department='" + department +
               "', claimsProcessed=" + claimsProcessed + "}";
    }
}

