/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterprise;

/**
 *
 * @author rdhan
 */
public class MedicalRecord {
    
    private int recordId;
    private int patientId;
    private int appointmentId; 
    private String diagnosis;
    private String treatment;
    private String notes;

    // Constructor with all fields
    public MedicalRecord(int recordId, int patientId, int appointmentId, String diagnosis, String treatment, String notes) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes = notes;
    }

    // Getters and Setters
    public int getRecordId() {
        return recordId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
}
