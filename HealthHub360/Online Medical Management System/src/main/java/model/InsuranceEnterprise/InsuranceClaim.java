/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.InsuranceEnterprise;
import java.util.Date;

/**
 *
 * @author AnjanaSruthiR
 */

public class InsuranceClaim {
    private int id;
    private int patientId; // References the Patient
    private int policyId; // References the InsurancePolicy
    private double claimAmount; // Amount being claimed
    private double approvedAmount; // Amount approved
    private String status; // Pending, Approved, Rejected
    private String remarks; // Approval or rejection remarks
    private int assignedManagerId; // InsuranceManager handling the claim
    private Date claimDate;
    private int processingTimeDays; // Number of days to process the claim

    // Constructor
    public InsuranceClaim(int id, int patientId, int policyId, double claimAmount, double approvedAmount,
                          String status, String remarks, int assignedManagerId, Date claimDate, int processingTimeDays) {
        this.id = id;
        this.patientId = patientId;
        this.policyId = policyId;
        this.claimAmount = claimAmount;
        this.approvedAmount = approvedAmount;
        this.status = status;
        this.remarks = remarks;
        this.assignedManagerId = assignedManagerId;
        this.claimDate = claimDate;
        this.processingTimeDays = processingTimeDays;
    }

    // Getters and Setters
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

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getAssignedManagerId() {
        return assignedManagerId;
    }

    public void setAssignedManagerId(int assignedManagerId) {
        this.assignedManagerId = assignedManagerId;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    public int getProcessingTimeDays() {
        return processingTimeDays;
    }

    public void setProcessingTimeDays(int processingTimeDays) {
        this.processingTimeDays = processingTimeDays;
    }

    @Override
    public String toString() {
        return "InsuranceClaim{id=" + id + ", patientId=" + patientId + ", policyId=" + policyId + 
               ", claimAmount=" + claimAmount + ", approvedAmount=" + approvedAmount +
               ", status='" + status + "', remarks='" + remarks + "', assignedManagerId=" + assignedManagerId + 
               ", claimDate=" + claimDate + ", processingTimeDays=" + processingTimeDays + "}";
    }
}

