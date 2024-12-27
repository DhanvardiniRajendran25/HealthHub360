/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelBillingEnterpriseDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelBillingEnterprise.PrescriptionBilling;
import ui.DatabaseUtil;
import static ui.DatabaseUtil.getConnection;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionBillingDAO {
    
    
    
       public void insertPrescriptionBilling(PrescriptionBilling billing) throws SQLException {
        String query = "INSERT INTO PrescriptionBilling (prescription_id, medicine_id, quantity, price, total_amount, bill_date) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, billing.getPrescriptionId());
            stmt.setInt(2, billing.getMedicineId());
            stmt.setInt(3, billing.getQuantity());
            stmt.setBigDecimal(4, billing.getPrice());
            stmt.setBigDecimal(5, billing.getTotalAmount());
            stmt.setDate(6, Date.valueOf(billing.getBillDate()));  // Assuming billDate is a LocalDate

            stmt.executeUpdate();
        } catch (SQLException e) {
            // Log the error and rethrow
            e.printStackTrace();
            throw e;
        }
    }
    
     public void createPrescriptionBilling(PrescriptionBilling billing, Connection conn) throws SQLException {
    System.out.println("createPrescriptionBilling: IN  ");
    String query = "INSERT INTO PrescriptionBilling (prescription_id, medicine_id, quantity, price, total_amount, bill_date) "
                 + "VALUES (?, ?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, billing.getPrescriptionId());
        stmt.setInt(2, billing.getMedicineId());
        stmt.setInt(3, billing.getQuantity());
        stmt.setBigDecimal(4, billing.getPrice());
        stmt.setBigDecimal(5, billing.getTotalAmount());
        stmt.setDate(6, Date.valueOf(billing.getBillDate()));
        
        System.out.println("createPrescriptionBilling: before executeUpdate");
        stmt.executeUpdate(); // This will run the insert query

        System.out.println("createPrescriptionBilling: out  "); // Commit transaction should happen here if needed
    } catch (SQLException e) {
        conn.rollback();  // Rollback if any error occurs
        throw e;  // Rethrow the exception
    }
}
     
      public ResultSet getBillingDetailsForPrescription(int prescriptionId, Connection conn) throws SQLException {
        // SQL query to fetch the billing details for a specific prescription
        String query = "SELECT pm.quantity, m.price, (pm.quantity * m.price) AS TotalPrice, pm.medicine_id "
                     + "FROM prescription_medicines pm "
                     + "JOIN medicines m ON pm.medicine_id = m.medicine_id "
                     + "WHERE pm.prescription_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, prescriptionId);  // Set the prescription ID in the query
            return stmt.executeQuery();  // Return the ResultSet to be used by the calling method
        }
    }
      
      public List<Object[]> getPrescriptionBillingSummary() throws SQLException {
        List<Object[]> summary = new ArrayList<>();

    // Updated query based on your correct query
    String query = "SELECT " +
                   "pres.id AS PrescriptionID, " +
                   "med.name AS MedicineName, " +
                   "med.price AS MedicinePrice, " +
                   "pb.quantity AS MedicineQuantity, " + "pres.payment_status,"+
                   "(pb.quantity * med.price) AS TotalPerMedicine, " +
                   "SUM(pb.quantity * med.price) OVER (PARTITION BY pres.id) AS TotalPrescriptionAmount " +
                   "FROM PrescriptionBilling pb " +
                   "JOIN medicines med ON med.id = pb.medicine_id " +
                   "JOIN prescriptions pres ON pres.id = pb.prescription_id";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Object[] row = new Object[]{
                rs.getInt("PrescriptionID"),
                rs.getString("MedicineName"),
                rs.getBigDecimal("MedicinePrice"),
                rs.getInt("MedicineQuantity"),
                rs.getBigDecimal("TotalPerMedicine"),
                rs.getBigDecimal("TotalPrescriptionAmount"),
                rs.getString("payment_status")
            };
            summary.add(row);
        }
    }

    return summary;
      
      }

      
      public List<Object[]> getBillingSummaryByUserId(int userId) throws SQLException {
        List<Object[]> summary = new ArrayList<>();

        String query = "SELECT " +
                       "pres.id AS PrescriptionID, " +
                       "med.name AS MedicineName, " +
                       "med.price AS MedicinePrice, " +
                       "pb.quantity AS MedicineQuantity, " +
                       "(pb.quantity * med.price) AS TotalPerMedicine, " +
                       "SUM(pb.quantity * med.price) OVER (PARTITION BY pres.id) AS TotalPrescriptionAmount, " +
                       "pres.payment_status " +
                       "FROM PrescriptionBilling pb " +
                       "JOIN medicines med ON med.id = pb.medicine_id " +
                       "JOIN prescriptions pres ON pres.id = pb.prescription_id " +
                       "JOIN patients p ON p.id = pres.patient_id " +
                       "JOIN users us ON us.person_id = p.person_id " +
                       "WHERE us.id = ?";

       try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query))  {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[]{
                        rs.getInt("PrescriptionID"),
                        rs.getString("MedicineName"),
                        rs.getBigDecimal("MedicinePrice"),
                        rs.getInt("MedicineQuantity"),
                        rs.getBigDecimal("TotalPerMedicine"),
                        rs.getBigDecimal("TotalPrescriptionAmount"),
                        rs.getString("payment_status")
                    };
                    summary.add(row);
                }
            }
        }
        return summary;
    }

   
}
