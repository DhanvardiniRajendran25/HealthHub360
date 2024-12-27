/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelPharmacyEnterprise.Medicine;
import modelPharmacyEnterprise.PrescriptionMedicine;
import ui.DatabaseUtil;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionMedicineDAO {

        public void create(PrescriptionMedicine prescriptionMedicine) throws SQLException {
   String query = "INSERT INTO prescription_medicines (prescription_id, medicine_id, quantity) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        System.out.println("Executing SQL Query: " + query);
        System.out.println("With Parameters: " +
            "Prescription ID: " + prescriptionMedicine.getPrescriptionId() +
            ", Medicine ID: " + prescriptionMedicine.getMedicineId() +
            ", Quantity: " + prescriptionMedicine.getQuantity());

        stmt.setInt(1, prescriptionMedicine.getPrescriptionId());
        stmt.setInt(2, prescriptionMedicine.getMedicineId());
        stmt.setInt(3, prescriptionMedicine.getQuantity());
        stmt.executeUpdate();

    } catch (SQLException e) {
        System.err.println("SQL Error while inserting PrescriptionMedicine: " + e.getMessage());
        throw e; // Rethrow to propagate to the caller
    }
}
        
         public List<Medicine> getMedicinesForPrescription(int prescriptionId) throws SQLException {
        String query = """
            SELECT m.name AS medicine_name, pm.quantity
            FROM prescription_medicines pm
            JOIN medicines m ON pm.medicine_id = m.id
            JOIN prescriptions p ON p.id = pm.prescription_id
            WHERE p.id = ?;
        """;
        
        List<Medicine> medicineList = new ArrayList<>();
        
        // Establish connection to the database
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, prescriptionId);  // Set the prescription ID parameter
            
            ResultSet rs = stmt.executeQuery();
            
            // Loop through the result set and map to Medicine objects
            while (rs.next()) {
                String medicineName = rs.getString("medicine_name");
                int quantity = rs.getInt("quantity");
                // Create a new Medicine object and add it to the list
                medicineList.add(new Medicine(medicineName, quantity)); 
            }
        }
        
        return medicineList;
    }
         
     
    
}
