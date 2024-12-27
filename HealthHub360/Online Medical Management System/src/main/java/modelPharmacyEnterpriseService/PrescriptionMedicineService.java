/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseService;
import java.sql.SQLException;
import modelPharmacyEnterpriseDAO.PrescriptionMedicineDAO;
import java.util.List;
import modelPharmacyEnterprise.Medicine;
import modelPharmacyEnterprise.PrescriptionMedicine;
/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionMedicineService {
    
     private final PrescriptionMedicineDAO prescriptionMedicineDAO;

    public PrescriptionMedicineService() {
        this.prescriptionMedicineDAO = new PrescriptionMedicineDAO();
    }
    

    
    // * Adds a new medicine to a prescription with validation.
     
    public void createPrescriptionMedicine(PrescriptionMedicine prescriptionMedicine) throws SQLException {
    System.out.println("Service Layer: Creating PrescriptionMedicine: " + prescriptionMedicine);
    prescriptionMedicineDAO.create(prescriptionMedicine);
}
    
    public List<Medicine> getMedicinesForPrescription(int prescriptionId) {
        try {
            return prescriptionMedicineDAO.getMedicinesForPrescription(prescriptionId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
  
    
}
