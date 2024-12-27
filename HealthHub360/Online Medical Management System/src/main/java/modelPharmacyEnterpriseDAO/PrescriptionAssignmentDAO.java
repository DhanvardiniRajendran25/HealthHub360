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
import java.util.ArrayList;
import java.util.List;
import modelPharmacyEnterprise.Prescription;
import modelPharmacyEnterprise.PrescriptionAssignment;
import ui.DatabaseUtil;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionAssignmentDAO {
    
    public void createAssignment(int prescriptionId, int pharmacistId, Date assignedDate) {
        
         
        String query = "INSERT INTO prescription_assignments (prescription_id, pharmacist_id, assigned_date, status) " +
                       "VALUES (?, ?, ?, 'Assigned')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, prescriptionId);
            stmt.setInt(2, pharmacistId);
            stmt.setDate(3, assignedDate);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
   
    
    public List<String> getAllPharmacistNames() {
        List<String> pharmacistNames = new ArrayList<>();
         String query = """
        SELECT u.username 
        FROM roles r 
        JOIN users u ON r.id = u.role_id 
        WHERE r.role_name = ?"""; // Dynamically filter by role name
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
             stmt.setString(1, "Pharmacist"); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pharmacistNames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pharmacistNames;
    }
    
    
    
     
    public int fetchPharmacistIdByName(String pharmacistName) throws SQLException {
        String query = "SELECT id FROM users WHERE username  = ?";
        
        try (Connection connection = DatabaseUtil.getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, pharmacistName);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Pharmacist not found: " + pharmacistName);
            }
        }
    }
    
    public String getAssignedPharmacistName(int prescriptionId) throws SQLException {
    String query = "SELECT u.username FROM users u "
                   + "JOIN prescription_assignments pa ON u.id = pa.pharmacist_id "
                   + "WHERE pa.prescription_id = ? ";
    
     try (Connection connection = DatabaseUtil.getConnection(); 
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        
        preparedStatement.setInt(1, prescriptionId);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if (resultSet.next()) {
            return resultSet.getString("username"); // Return the pharmacist's name
        } else {
            return null; // No pharmacist assigned yet
        }
    }
}
    
      public Prescription fetchPrescriptionById(int prescriptionId) throws SQLException {
    String query = "SELECT p.id, p.patient_name, p.doctor_name, p.pharmacy_manager_id, p.date_requested, p.status "
                   + "FROM prescriptions p WHERE p.id = ?";
    try (Connection connection = DatabaseUtil.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        
        preparedStatement.setInt(1, prescriptionId);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if (resultSet.next()) {
            // Create a Prescription object using the constructor
            int id = resultSet.getInt("id");
            String patientName = resultSet.getString("patient_name");
            String doctorName = resultSet.getString("doctor_name");
            int pharmacyManagerId = resultSet.getInt("pharmacy_manager_id");
            Date dateRequested = resultSet.getDate("date_requested");
            String status = resultSet.getString("status");
            
            // Return a new Prescription object created with the constructor
            return new Prescription(id, patientName, doctorName, pharmacyManagerId, dateRequested, status);
        } else {
            return null; // If no prescription found, return null
        }
    }
}
      
      
      public String fetchAssignedPharmacistNameByPrescriptionId(int prescriptionId) throws SQLException {
    String query = "SELECT u.username FROM prescription_assignments pa "
                 + "JOIN users u ON pa.pharmacist_id = u.id "
                 + "WHERE pa.prescription_id = ?";
    
    try (Connection connection = DatabaseUtil.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        
        preparedStatement.setInt(1, prescriptionId);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if (resultSet.next()) {
            return resultSet.getString("username"); // Assuming the pharmacist's name is in the 'username' column of the 'users' table
        } else {
            return "Not Assigned"; // In case no pharmacist is assigned
        }
    }
}

    public List<String> getAllPharmacyManager() {
        List<String> pharmacistNames = new ArrayList<>();
        String query = """
        SELECT u.username 
        FROM roles r 
        JOIN users u ON r.id = u.role_id 
        WHERE r.role_name = ?"""; // Dynamically filter by role name
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
             stmt.setString(1, "Pharmacy Manager"); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pharmacistNames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pharmacistNames;
        
        
        
    }

    public List<String> getAllPatient() {
        
         List<String> Patient = new ArrayList<>();
        String query = """
        SELECT u.username 
        FROM roles r 
        JOIN users u ON r.id = u.role_id 
        WHERE r.role_name = ?"""; // Dynamically filter by role name
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
             stmt.setString(1, "Patient"); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Patient.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Patient;
        
    }
     
    
    public int getUserIdByNameAndRole(String username, String roleName) throws SQLException {
     String sql = """
        SELECT u.id
        FROM users u
        JOIN roles r ON u.role_id = r.id
        JOIN persons p ON u.person_id = p.id
        WHERE u.username = ? AND r.role_name = ?
    """;

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username);
        stmt.setString(2, roleName);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        } else {
            // No user found
            throw new IllegalArgumentException("No " + roleName + " found with name: " + username);
        }
    } catch (SQLException ex) {
        throw new SQLException("Database error while fetching user ID: " + ex.getMessage(), ex);
    }

    }
    
    
    public int getPatientIdByUsername(String username) throws SQLException {
    String sql = """
        SELECT p.id
        FROM patients p
        JOIN users u ON p.person_id = u.person_id
        WHERE u.username = ?
    """;

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username); // Set the username parameter
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id"); // Return the patient ID
        } else {
            // No patient found
            throw new IllegalArgumentException("No patient found with username: " + username);
        }
    } catch (SQLException ex) {
        // Handle database errors
        throw new SQLException("Database error while fetching patient ID: " + ex.getMessage(), ex);
    }
}
    
       public int getDoctorIdByUserId(int userId) throws SQLException {
        String query = "SELECT d.id AS doctor_id " +
                       "FROM users us " +
                       "JOIN doctors d ON d.person_id = us.person_id " +
                       "WHERE us.id = ?";

         try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("doctor_id");
                } else {
                    throw new SQLException("No doctor found for the given user ID: " + userId);
                }
            }
        }
    }
    
        
    
}

