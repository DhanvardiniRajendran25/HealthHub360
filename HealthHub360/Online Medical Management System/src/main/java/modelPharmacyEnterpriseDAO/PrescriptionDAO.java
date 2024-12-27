/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import modelPharmacyEnterprise.Prescription;
import ui.DatabaseUtil;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionDAO {
    
  public int create(Prescription prescription) throws SQLException {

    String query = "INSERT INTO prescriptions (patient_id, doctor_id, pharmacy_manager_id, date_requested, status,payment_status) " +
                   "VALUES (?, ?, ?, ?, ?,?)";

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, prescription.getPatientId());
        stmt.setInt(2, prescription.getDoctorId());
        stmt.setInt(3, prescription.getPharmacyManagerId());
        stmt.setDate(4, prescription.getDateRequested());
        stmt.setString(5, prescription.getStatus());

        stmt.setString(6, prescription.getPaymentStatus());
        stmt.executeUpdate();

        try (ResultSet keys = stmt.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1); // Return the generated prescription ID
            }
        }
    }
    throw new SQLException("Failed to create prescription, no ID obtained.");
}
     
  public List<Prescription> getPrescriptionsByPharmacyManagerId(int pharmacyManagerId) {
    List<Prescription> prescriptions = new ArrayList<>();
    
    // Define the SQL query with the required JOINs
    String query = "SELECT p.id, " +
                   "per_patient.name AS patient_name, " +
                   "per_doctor.name AS doctor_name, " +
                   "p.pharmacy_manager_id, " +
                   "p.date_requested, " +
                   "p.status " +
                   "FROM prescriptions p " +
                   "JOIN patients pa ON pa.id = p.patient_id " +
                   "JOIN persons per_patient ON pa.person_id = per_patient.id " +
                   "JOIN doctors doc ON doc.id = p.doctor_id " +
                   "JOIN persons per_doctor ON per_doctor.id = doc.person_id " +
                   "WHERE p.pharmacy_manager_id = ?";  // Parameterized query to prevent SQL injection

    try (Connection conn = DatabaseUtil.getConnection(); // Open connection to the database
         PreparedStatement stmt = conn.prepareStatement(query)) { // Prepare the SQL statement
        stmt.setInt(1, pharmacyManagerId);  // Set the pharmacyManagerId parameter
        
        try (ResultSet rs = stmt.executeQuery()) {  // Execute the query and get the result set
            while (rs.next()) {
                // Retrieve the values from the result set
                int prescriptionId = rs.getInt("id");
                String patientName = rs.getString("patient_name");
                String doctorName = rs.getString("doctor_name");
                Date dateRequested = rs.getDate("date_requested");
                String status = rs.getString("status");

                // Create a new Prescription object with the retrieved values
                    Prescription prescription = new Prescription(
                        prescriptionId,      // Prescription ID
                        patientName,         // Patient Name
                        doctorName,          // Doctor Name
                        pharmacyManagerId,   // Pharmacy Manager ID (we don't retrieve it because it's used for filtering)
                        dateRequested,       // Date Requested
                        status               // Prescription Status
                );

                // Add the newly created prescription to the list
                prescriptions.add(prescription);
            }
        }
    } catch (SQLException e) {
        // Log the exception for debugging
        e.printStackTrace();
    }
    
    // Return the list of prescriptions
    return prescriptions;
}



    public void update(Prescription prescription) {
        String query = "UPDATE prescriptions SET patient_id = ?, doctor_id = ?, pharmacy_manager_id = ?, " +
                       "date_requested = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, prescription.getPatientId());
            stmt.setInt(2, prescription.getDoctorId());
            stmt.setInt(3, prescription.getPharmacyManagerId());
            stmt.setDate(4, prescription.getDateRequested());
            stmt.setString(5, prescription.getStatus());
            stmt.setInt(6, prescription.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateStatus(int id, String status) {
    String query = "UPDATE prescriptions SET status = ? WHERE id = ?";
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, status);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
     public void updatePaymentStatus(int id, String status) {
    String query = "UPDATE prescriptions SET payment_status = ? WHERE id = ?";
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, status);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void delete(int id) {
        String query = "DELETE FROM prescriptions WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*To reteieve the records based on the pharmacist */
     public List<Object[]> getPrescriptionsAssignedtoPharmacist(int pharmacistId) throws SQLException {
        String query = """
            SELECT p.id AS prescription_id,
                   per_patient.name AS patient_name, 
                   per_doctor.name AS doctor_name, 
                   p.pharmacy_manager_id, 
                   pass.assigned_date,

                   p.status,
                   p.payment_status

            FROM prescriptions p 
            JOIN prescription_assignments pass ON pass.prescription_id = p.id
            JOIN patients pa ON pa.id = p.patient_id 
            JOIN persons per_patient ON pa.person_id = per_patient.id 
            JOIN doctors doc ON doc.id = p.doctor_id
            JOIN persons per_doctor ON per_doctor.id = doc.person_id
            WHERE pass.pharmacist_id = ?;
        """;

        List<Object[]> data = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, pharmacistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getInt("prescription_id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getDate("assigned_date"),

                    rs.getString("status"),
                    rs.getString("payment_status")

                };
                data.add(row);
            }
        }
        
        System.out.println("Pharmacy Managers loaded successfully: " + data);
        return data;

    } 
     
      public Prescription getPrescriptionById(int prescriptionId) {
        Prescription prescription = null;
        String query = "SELECT patient_id, doctor_id, pharmacy_manager_id, date_requested, status,payment_status  FROM prescriptions WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, prescriptionId); // Set the prescription ID in the query

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create Prescription object and populate its fields from the result set
                    prescription = new Prescription(
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("pharmacy_manager_id"),
                        rs.getDate("date_requested"),
                        rs.getString("status"),
                            rs.getString("payment_status")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prescription;
    }
      
     

public List<Object[]> getPrescriptionsForPatient(int patientId) throws SQLException {
      // First, fetch the doctor_id based on the user_id
    int patientid = getPatientId(patientId);

    
    // If no doctor_id is found, return an empty list
    if (patientid == -1) {
        return new ArrayList<>();
    }
    
    // Then, fetch the prescription details for the retrieved doctor_id
    String query = """
        SELECT p.id AS prescription_id,
               per_patient.name AS patient_name, 
               per_doctor.name AS doctor_name, 
               p.pharmacy_manager_id, 
               p.status,
               p.payment_status
        FROM prescriptions p 
        JOIN patients pa ON pa.id = p.patient_id 
        JOIN persons per_patient ON pa.person_id = per_patient.id 
        JOIN doctors doc ON doc.id = p.doctor_id
        JOIN persons per_doctor ON per_doctor.id = doc.person_id

        WHERE pa.id = ?;  -- Use the patient directly

    """;

    List<Object[]> data = new ArrayList<>();

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, patientid);  // Set the doctor_id for the query
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            // Use Integer for prescription_id and pharmacy_manager_id as they are integers in the DB
            Object[] row = new Object[]{
                rs.getInt("prescription_id"),  // Integer value
                rs.getString("patient_name"),  // String value
                rs.getString("doctor_name"),   // String value
                rs.getInt("pharmacy_manager_id"),  // Integer value
                rs.getString("status"),   // String value
                rs.getString("payment_status")  // String value
            };
            data.add(row);
        }
    }



    return data;
}

public List<Object[]> getPrescriptionsForDoctor(int userId) throws SQLException {
    // First, fetch the doctor_id based on the user_id
    int doctorId = getDoctorId(userId);
    
    // If no doctor_id is found, return an empty list
    if (doctorId == -1) {
        return new ArrayList<>();
    }
    
    // Then, fetch the prescription details for the retrieved doctor_id
    String query = """
        SELECT p.id AS prescription_id,
               per_patient.name AS patient_name, 
               per_doctor.name AS doctor_name, 
               p.pharmacy_manager_id, 
               p.status,
               p.payment_status
        FROM prescriptions p 
        JOIN patients pa ON pa.id = p.patient_id 
        JOIN persons per_patient ON pa.person_id = per_patient.id 
        JOIN doctors doc ON doc.id = p.doctor_id
        JOIN persons per_doctor ON per_doctor.id = doc.person_id
        WHERE doc.id = ?;  -- Use the doctor_id directly
    """;

    List<Object[]> data = new ArrayList<>();

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, doctorId);  // Set the doctor_id for the query
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            // Use Integer for prescription_id and pharmacy_manager_id as they are integers in the DB
            Object[] row = new Object[]{
                rs.getInt("prescription_id"),  // Integer value
                rs.getString("patient_name"),  // String value
                rs.getString("doctor_name"),   // String value
                rs.getInt("pharmacy_manager_id"),  // Integer value
                rs.getString("status"),   // String value
                rs.getString("payment_status")  // String value
            };
            data.add(row);
        }
    }

    return data;
}
private int getDoctorId(int userId) throws SQLException {
    String query = """
        SELECT d.id AS doctor_id
        FROM users us
        JOIN doctors d ON d.person_id = us.person_id
        WHERE us.id = ?;
    """;

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, userId);  // Set the user_id
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("doctor_id");  // Return the doctor_id
        }
    }

    return -1;  // Return -1 if no doctor_id is found
}


public int getPatientId(int userId) throws SQLException {
    String query = """
        SELECT p.id AS patient_id
        FROM users u
        JOIN patients p ON p.person_id = u.person_id
        WHERE u.id = ?;
    """;

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, userId); // Set user_id
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("patient_id"); // Return the patient_id
        }
    }

    return -1; // Return -1 if no patient_id is found
}
        
}
  

