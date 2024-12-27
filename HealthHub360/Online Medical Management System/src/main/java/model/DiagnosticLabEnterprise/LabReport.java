/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterprise;
import java.util.Date;

/**
 *
 * @author AnjanaSruthiR
 */

public class LabReport {
    private int id;
    private int testRequestId;
    private int labTechnicianId;
    private String result;
    private Date generatedDate;

    // Constructor
    public LabReport(int id, int testRequestId, int labTechnicianId, String result, Date generatedDate) {
        this.id = id;
        this.testRequestId = testRequestId;
        this.labTechnicianId = labTechnicianId;
        this.result = result;
        this.generatedDate = generatedDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(int testRequestId) {
        this.testRequestId = testRequestId;
    }

    public int getLabTechnicianId() {
        return labTechnicianId;
    }

    public void setLabTechnicianId(int labTechnicianId) {
        this.labTechnicianId = labTechnicianId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    @Override
    public String toString() {
        return "LabReport{id=" + id + ", testRequestId=" + testRequestId + 
               ", labTechnicianId=" + labTechnicianId + ", result='" + result + 
               "', generatedDate=" + generatedDate + "}";
    }
}
