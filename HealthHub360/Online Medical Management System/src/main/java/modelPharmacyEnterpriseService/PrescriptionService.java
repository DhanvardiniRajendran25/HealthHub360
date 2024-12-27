/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseService;

import modelPharmacyEnterpriseDAO.PrescriptionDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import modelPharmacyEnterprise.Prescription;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionService {
      private final PrescriptionDAO prescriptionDAO;

    // Constructor: Inject the DAO dependency
    public PrescriptionService(PrescriptionDAO prescriptionDAO) {
        this.prescriptionDAO = prescriptionDAO;
    }
    
    public List<Prescription> getPrescriptionsForPharmacyManager(int pharmacyManagerId) {
        return prescriptionDAO.getPrescriptionsByPharmacyManagerId(pharmacyManagerId);
    }
   public int createPrescription(Prescription prescription) throws SQLException {
    return prescriptionDAO.create(prescription);
}

   // Update a prescription
   public void updatePrescriptionStatus(int id, String status) {
    prescriptionDAO.updateStatus(id, status);
 }
   
   public void updatePrescriptionPaymentStatus(int id, String status) {
    prescriptionDAO.updatePaymentStatus(id, status);
 }
    // Delete a prescription
    public void deletePrescription(int id) {
        prescriptionDAO.delete(id);
    }
    
    /*Used to return the prescription assignment with prescription details to display in the pharmacist dashboard*/
     public List<Object[]> getPrescriptionsByPharmacistId(int pharmacistId) throws SQLException {
        return prescriptionDAO.getPrescriptionsAssignedtoPharmacist(pharmacistId);
    }
     
     public List<Object[]> getPrescriptionsByDocotorId(int userid) throws SQLException {
        return prescriptionDAO.getPrescriptionsForDoctor(userid);
    }
     

      public List<Object[]> getPrescriptionsByPaitentId(int userid) throws SQLException {
        return prescriptionDAO.getPrescriptionsForPatient(userid);

    }
     
    public List<Object[]> getPrescriptionsByPharmacistIds(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    // Method to get a prescription by ID
    public Prescription getPrescriptionById(int prescriptionId) {
        return prescriptionDAO.getPrescriptionById(prescriptionId); // Delegate to DAO
    }

}
