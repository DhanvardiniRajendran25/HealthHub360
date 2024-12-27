/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelBillingEnterpriseService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import modelBillingEnterprise.PrescriptionBilling;
import modelBillingEnterpriseDAO.PrescriptionBillingDAO;
import modelPharmacyEnterprise.Medicine;
import modelPharmacyEnterprise.PrescriptionMedicine;
import modelPharmacyEnterpriseService.MedicineService;
import ui.DatabaseUtil;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionBillingService {
    
     private PrescriptionBillingDAO billingDAO;
    // Constructor accepting PrescriptionBillingDAO
    // Constructor accepting PrescriptionBillingDAO
    public PrescriptionBillingService(PrescriptionBillingDAO billingDAO) {
        this.billingDAO = billingDAO;  // Initialize the billingDAO
    }
        
    public void generateBillingForPrescription(int prescriptionId) throws SQLException {
    
    } 
    
    public void createPrescriptionBilling(PrescriptionBilling billing) throws SQLException {
        // Validate input (optional)
        if (billing == null) {
            throw new IllegalArgumentException("Billing information cannot be null.");
        }

        // Call the DAO method to save the billing record into the database
        billingDAO.insertPrescriptionBilling(billing);
    }
    
       public List<Object[]> fetchBillingSummary() throws SQLException {
        return billingDAO.getPrescriptionBillingSummary();
    }

       
     public List<Object[]> getBillingSummaryByUserId(int userId) throws SQLException {
        return billingDAO.getBillingSummaryByUserId(userId);
    } 

     

   
  
}

