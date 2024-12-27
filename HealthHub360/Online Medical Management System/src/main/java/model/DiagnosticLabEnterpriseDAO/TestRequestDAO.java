/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterpriseDAO;

import model.DiagnosticLabEnterprise.TestRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ui.DatabaseUtil;

/**
 *
 * @author AnjanaSruthiR
 */
public class TestRequestDAO {

    private Connection connection;

    public TestRequestDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new TestRequest
    public void createTestRequest(TestRequest testRequest) throws SQLException {
        String query = "INSERT INTO test_requests (patient_id, doctor_id, lab_test_id, manager_id, technician_id, status, request_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, testRequest.getPatientId());
            stmt.setInt(2, testRequest.getDoctorId());
            stmt.setInt(3, testRequest.getLabTestId());
            stmt.setInt(4, testRequest.getManagerId());
            stmt.setInt(5, testRequest.getTechnicianId());
            stmt.setString(6, testRequest.getStatus());
            stmt.setTimestamp(7, new Timestamp(testRequest.getRequestDate().getTime()));
            stmt.executeUpdate();
        }
    }

    // Get a TestRequest by ID
    public TestRequest getTestRequestById(int id) throws SQLException {
        String query = "SELECT * FROM test_requests WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TestRequest(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("lab_test_id"),
                        rs.getInt("manager_id"),
                        rs.getInt("technician_id"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date")
                );
            }
        }
        return null;
    }

    // Get all TestRequests
    public List<TestRequest> getAllTestRequests() throws SQLException {
        List<TestRequest> testRequests = new ArrayList<>();
        String query = "SELECT * FROM test_requests";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                testRequests.add(new TestRequest(
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
        return testRequests;
    }

    public List<TestRequest> getTestRequestsForManager(int managerId, String status) throws SQLException {
        String query = "SELECT * FROM test_requests WHERE manager_id = ?";

        // If a status filter is provided, add it to the query
        if (status != null) {
            query += " AND status = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, managerId);
            if (status != null) {
                stmt.setString(2, status);
            }

            ResultSet rs = stmt.executeQuery();
            List<TestRequest> requests = new ArrayList<>();
            while (rs.next()) {
                requests.add(mapResultSetToTestRequest(rs));
            }
            return requests;
        }
    }

    // Update a TestRequest
    public void updateTestRequest(TestRequest testRequest) throws SQLException {
        String query = "UPDATE test_requests SET patient_id = ?, doctor_id = ?, lab_test_id = ?, manager_id = ?, technician_id = ?, "
                + "status = ?, request_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, testRequest.getPatientId());
            stmt.setInt(2, testRequest.getDoctorId());
            stmt.setInt(3, testRequest.getLabTestId());
            stmt.setInt(4, testRequest.getManagerId());
            stmt.setInt(5, testRequest.getTechnicianId());
            stmt.setString(6, testRequest.getStatus());
            stmt.setTimestamp(7, new Timestamp(testRequest.getRequestDate().getTime()));
            stmt.setInt(8, testRequest.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a TestRequest
    public void deleteTestRequest(int id) throws SQLException {
        String query = "DELETE FROM test_requests WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getFilteredTestRequests(int technicianId, String status) throws SQLException {
        List<Map<String, Object>> testRequests = new ArrayList<>();
        String query = "SELECT tr.id AS RequestID, lt.name AS TestType, p.name AS PatientName, tr.status AS Status "
                + "FROM test_requests tr "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN patients pt ON tr.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "WHERE tr.technician_id = ?";

        if (status != null && !status.equalsIgnoreCase("All")) {
            query += " AND tr.status = ?";
        }

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            if (status != null && !status.equalsIgnoreCase("All")) {
                stmt.setString(2, status);
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> testRequest = new HashMap<>();
                testRequest.put("RequestID", rs.getInt("RequestID"));
                testRequest.put("TestType", rs.getString("TestType"));
                testRequest.put("PatientName", rs.getString("PatientName"));
                testRequest.put("Status", rs.getString("Status"));
                testRequests.add(testRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching filtered test requests: " + e.getMessage(), e);
        }
        return testRequests;
    }

    private TestRequest mapResultSetToTestRequest(ResultSet rs) throws SQLException {
        return new TestRequest(
                rs.getInt("id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getInt("lab_test_id"),
                rs.getInt("manager_id"),
                rs.getObject("technician_id") != null ? rs.getInt("technician_id") : 0,
                rs.getString("status"),
                rs.getTimestamp("request_date")
        );
    }

    public List<Map<String, Object>> getNewRequestsForTechnician(int technicianId) throws SQLException {
        String query = """
        SELECT id, lab_test_id, patient_id, request_date, status
        FROM test_requests
        WHERE technician_id = ? AND status = 'Assigned'
    """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> requests = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> request = new HashMap<>();
                    request.put("ID", rs.getInt("id"));
                    request.put("Type", rs.getString("request_type"));
                    request.put("Description", rs.getString("description"));
                    request.put("Date", rs.getDate("assigned_date"));
                    requests.add(request);
                }
                return requests;
            }
        }
    }

    public List<Map<String, Object>> getRequestsByStatus(int managerId, String status) throws SQLException {
        String query = """
        SELECT id, lab_test_id, patient_id, request_date, status
        FROM test_requests
        WHERE manager_id = ? AND status = ?
    """;
        List<Map<String, Object>> requests = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, managerId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> request = new HashMap<>();
                    request.put("ID", rs.getInt("id"));
                    request.put("LabTestID", rs.getInt("lab_test_id"));
                    request.put("PatientID", rs.getInt("patient_id"));
                    request.put("RequestDate", rs.getDate("request_date"));
                    request.put("Status", rs.getString("status"));
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query + "\n" + e.getMessage());
            throw e; // Re-throw for higher-level handling
        }
        return requests;
    }

    public List<Map<String, Object>> getRequestsByTechnicianId(int technicianId) throws SQLException {
        List<Map<String, Object>> testRequests = new ArrayList<>();

        String query = "SELECT tr.id AS RequestID, lt.name AS TestType, p.name AS PatientName, tr.status AS Status "
                + "FROM test_requests tr "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN patients pt ON tr.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "WHERE tr.technician_id = ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> testRequest = new HashMap<>();
                testRequest.put("RequestID", rs.getInt("RequestID"));
                testRequest.put("TestType", rs.getString("TestType"));
                testRequest.put("PatientName", rs.getString("PatientName"));
                testRequest.put("Status", rs.getString("Status"));
                testRequests.add(testRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching test requests: " + e.getMessage(), e);
        }
        return testRequests;
    }

    public boolean updateRequestStatus(int requestId, String newStatus) throws SQLException {
        String query = "UPDATE test_requests SET status = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating request status: " + e.getMessage(), e);
        }
    }

    public String getTestDescriptionByRequestId(int requestId) {
        String query = "SELECT lt.description AS TestDescription "
                + "FROM test_requests tr "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "WHERE tr.id = ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("TestDescription");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTestRequestDetails(int requestId) throws SQLException {
        String query = "SELECT tr.id AS RequestID, lt.name AS TestType, lt.description AS TestDescription, "
                + "p.name AS PatientName, p.contact_number AS PatientContact, tr.status AS Status "
                + "FROM test_requests tr "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN patients pt ON tr.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "WHERE tr.id = ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "Request ID: " + rs.getInt("RequestID") + "\n"
                        + "Test Type: " + rs.getString("TestType") + "\n"
                        + "Description: " + rs.getString("TestDescription") + "\n"
                        + "Patient Name: " + rs.getString("PatientName") + "\n"
                        + "Contact: " + rs.getString("PatientContact") + "\n"
                        + "Status: " + rs.getString("Status");
            } else {
                throw new SQLException("Test request not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching test request details: " + e.getMessage(), e);
        }
    }

}
