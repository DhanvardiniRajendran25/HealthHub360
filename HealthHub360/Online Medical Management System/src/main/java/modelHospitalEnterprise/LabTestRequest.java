/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterprise;

/**
 *
 * @author rdhan
 */
public class LabTestRequest {
    
    private int requestId;
    private int patientId;
    private int doctorId;
    private String testName;
    private String status;

    public LabTestRequest(int requestId, int patientId, int doctorId, String testName, String status) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.testName = testName;
        this.status = status;
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LabTestRequest{" +
                "requestId=" + requestId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", testName='" + testName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    
}
