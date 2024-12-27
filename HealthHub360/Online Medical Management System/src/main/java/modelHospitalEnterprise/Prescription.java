/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterprise;

/**
 *
 * @author rdhan
 */
public class Prescription {
    
    private int prescriptionId;
    private int medicalRecordId;
    private int patientId;
    private int doctorId;
    private String medication;
    private String dosage;
    private String instructions;

    public Prescription(int prescriptionId, int patientId, int doctorId, String medication, String dosage, String instructions) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    // Getters and Setters
    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    
    public int getMedicalRecordId() {
    return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
    this.medicalRecordId = medicalRecordId;
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

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "prescriptionId=" + prescriptionId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", medication='" + medication + '\'' +
                ", dosage='" + dosage + '\'' +
                ", instructions='" + instructions + '\'' +
                '}';
    }
    
}
