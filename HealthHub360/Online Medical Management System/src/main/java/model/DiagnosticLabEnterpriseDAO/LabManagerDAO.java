/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterpriseDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.DiagnosticLabEnterprise.LabManager;
import model.DiagnosticLabEnterprise.TestRequest;
import model.DiagnosticLabEnterprise.LabTechnician;

/**
 *
 * @author AnjanaSruthiR
 */

public class LabManagerDAO {
    private Connection connection;

    public LabManagerDAO(Connection connection) {
        this.connection = connection;
    }

    // Fetch a specific Lab Manager by ID
    public LabManager getLabManagerById(int managerId) throws SQLException {
        String query = "SELECT * FROM users u " +
                       "JOIN persons p ON u.person_id = p.id " +
                       "WHERE u.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, managerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new LabManager(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    "Pathology", // Placeholder for department
                    5            // Placeholder for experience
                );
            }
        }
        return null;
    }

    // Retrieve all test requests assigned to this Lab Manager
    public List<TestRequest> getTestRequestsForManager(int managerId) throws SQLException {
        List<TestRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM test_requests WHERE manager_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, managerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(new TestRequest(
                    rs.getInt("id"),
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getInt("lab_test_id"),
                    rs.getInt("manager_id"),
                    rs.getInt("technician_id"),
                    rs.getString("status"),
                    rs.getTimestamp("request_date")
                ));
            }
        }
        return requests;
    }

    // Assign a test request to a Lab Technician
    public void assignTechnicianToRequest(int requestId, int technicianId) throws SQLException {
        String query = "UPDATE test_requests SET technician_id = ?, status = 'Assigned' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
        }
    }

//    public void updateRequestStatus(int requestId, String status) throws SQLException {
//        String query = "UPDATE test_requests SET status = ? WHERE id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, status);
//            stmt.setInt(2, requestId);
//            stmt.executeUpdate();
//        }
//    }

}
