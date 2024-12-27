/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseService;

import modelHospitalEnterpriseDAO.PrescriptionDAO;
import modelHospitalEnterprise.Prescription;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author rdhan
 */
public class PrescriptionService {
    
    private PrescriptionDAO prescriptionDAO;

    public PrescriptionService() throws SQLException {
        this.prescriptionDAO = new PrescriptionDAO();
    }

    // Add a new prescription
    public boolean createPrescription(Prescription prescription) {
        try {
            return prescriptionDAO.addPrescription(prescription);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch prescriptions for a specific patient
    public List<Prescription> getPrescriptionsByPatient(int patientId) throws SQLException {
        return prescriptionDAO.getPrescriptionsByPatient(patientId);
    }

    // Fetch all prescriptions
    public List<Prescription> getAllPrescriptions() throws SQLException {
        return prescriptionDAO.getAllPrescriptions();
    }
    
}
