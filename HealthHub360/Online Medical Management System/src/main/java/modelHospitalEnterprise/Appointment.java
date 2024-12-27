/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterprise;

/**
 *
 * @author rdhan
 */
public class Appointment {
    
    private int medicalRecordId;
    private int appointmentId;               // Unique appointment ID
    private int doctorId;                    // ID of the doctor for the appointment
    private int patientId;                   // ID of the patient for the appointment
    private String doctorName;               // Name of the doctor
    private String patientName;              // Name of the patient
    private String dateTime;                 // Appointment date and time (e.g., "2024-12-06 10:30:00")
    private String status;                   // Status of the appointment (e.g., "Scheduled", "Completed", "Cancelled")
    private String notes;                    // Additional notes for the appointment

    // Default Constructor
    public Appointment() {}

    // Parameterized Constructor
    public Appointment(int appointmentId, String doctorName, String patientName, String dateTime) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.dateTime = dateTime;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Utility Method: Display Appointment Details
    @Override
    public String toString() {
        return "Appointment{" +
               "appointmentId=" + appointmentId +
               ", doctorId=" + doctorId +
               ", patientId=" + patientId +
               ", doctorName='" + doctorName + '\'' +
               ", patientName='" + patientName + '\'' +
               ", dateTime='" + dateTime + '\'' +
               ", status='" + status + '\'' +
               ", notes='" + notes + '\'' +
               '}';
    }
    
}
