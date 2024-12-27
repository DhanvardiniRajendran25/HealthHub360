/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Patient;
import modelHospitalEnterprise.Appointment;
import modelHospitalEnterprise.MedicalRecord;
import modelHospitalEnterprise.Prescription;
import modelHospitalEnterprise.LabTestRequest;
import modelHospitalEnterpriseDAO.PatientDAO;
import modelHospitalEnterpriseDAO.AppointmentDAO;
import modelHospitalEnterpriseDAO.MedicalRecordDAO;
import modelHospitalEnterpriseDAO.PrescriptionDAO;
import modelHospitalEnterpriseDAO.LabTestRequestDAO;

/**
 *
 * @author rdhan
 */
public class PatientService {
    
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private PrescriptionDAO prescriptionDAO;
    private LabTestRequestDAO labTestRequestDAO;

    public PatientService() throws SQLException {
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.prescriptionDAO = new PrescriptionDAO();
        this.labTestRequestDAO = new LabTestRequestDAO();
    }

    // 1. Register a patient
    public boolean registerPatient(Patient patient) {
        return patientDAO.addPatient(patient);
    }

    // 2. View available doctors and their schedules
    public List<Appointment> viewAvailableDoctorsAndSchedules() {
        try {
            return appointmentDAO.getAvailableAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list on failure
        }
    }

    // 3. Book an appointment
    public boolean bookAppointment(Appointment appointment) {
        try {
            return appointmentDAO.addAppointment(appointment);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. View medical history (appointments, prescriptions, lab reports)
    public List<MedicalRecord> viewMedicalHistory(int patientId) {
        return medicalRecordDAO.getMedicalRecordsByPatientId(patientId);
    }

    public List<Prescription> viewPrescriptions(int patientId) {
        try {
            return prescriptionDAO.getPrescriptionsByPatient(patientId); // Fixed method name
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<LabTestRequest> viewLabReports(int patientId) throws SQLException {
        return labTestRequestDAO.getLabTestRequestsByPatientId(patientId);
    }

    // 5. Handle pharmacy and lab requests
    public boolean requestLabTest(LabTestRequest labTestRequest) {
        try {
            return labTestRequestDAO.addLabTestRequest(labTestRequest);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean receivePrescription(Prescription prescription) {
        try {
            return prescriptionDAO.addPrescription(prescription);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
