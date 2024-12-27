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

public class TestRequest {
    private int id;
    private int patientId;
    private Integer doctorId; // Nullable for self-requested tests
    private int labTestId;
    private int managerId;
    private int technicianId;
    private String status; // Now a String instead of RequestStatus
    private Date requestDate;

    // Constructor
    public TestRequest(int id, int patientId, Integer doctorId, int labTestId, int managerId, int technicianId,
                       String status, Date requestDate) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.labTestId = labTestId;
        this.managerId = managerId;
        this.technicianId = technicianId;
        this.status = status; // Accepts any string value
        this.requestDate = requestDate;
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

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public int getLabTestId() {
        return labTestId;
    }

    public void setLabTestId(int labTestId) {
        this.labTestId = labTestId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(int technicianId) {
        this.technicianId = technicianId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status; // You can directly set the string value
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public String toString() {
        return "TestRequest{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", labTestId=" + labTestId +
                ", managerId=" + managerId +
                ", technicianId=" + technicianId +
                ", status='" + status + '\'' +
                ", requestDate=" + requestDate +
                '}';
    }
}
