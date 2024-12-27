/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterpriseDAO;
import model.DiagnosticLabEnterprise.LabReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ui.DatabaseUtil;

/**
 *
 * @author AnjanaSruthiR
 */

public class LabReportDAO {
    private Connection connection;

    public LabReportDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new LabReport
    public void createLabReport(LabReport labReport) throws SQLException {
        String query = "INSERT INTO lab_reports (test_request_id, lab_technician_id, result, generated_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, labReport.getTestRequestId());
            stmt.setInt(2, labReport.getLabTechnicianId());
            stmt.setString(3, labReport.getResult());
            stmt.setTimestamp(4, new Timestamp(labReport.getGeneratedDate().getTime()));
            stmt.executeUpdate();
        }
    }

    // Get a LabReport by ID
    public LabReport getLabReportById(int id) throws SQLException {
        String query = "SELECT * FROM lab_reports WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new LabReport(
                        rs.getInt("id"),
                        rs.getInt("test_request_id"),
                        rs.getInt("lab_technician_id"),
                        rs.getString("result"),
                        rs.getTimestamp("generated_date")
                );
            }
        }
        return null;
    }

    // Get all LabReports
    public List<LabReport> getAllLabReports() throws SQLException {
        List<LabReport> labReports = new ArrayList<>();
        String query = "SELECT * FROM lab_reports";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                labReports.add(new LabReport(
                        rs.getInt("id"),
                        rs.getInt("test_request_id"),
                        rs.getInt("lab_technician_id"),
                        rs.getString("result"),
                        rs.getTimestamp("generated_date")
                ));
            }
        }
        return labReports;
    }

    // Update a LabReport
    public boolean updateLabReport(int reportId, String newResult) throws SQLException {
        String query = "UPDATE lab_reports SET result = ?, generated_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newResult);
            stmt.setTimestamp(2, new Timestamp(new java.util.Date().getTime())); // Update the generated date
            stmt.setInt(3, reportId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<LabReport> getLabReportsByTechnician(int technicianId) throws SQLException {
        List<LabReport> labReports = new ArrayList<>();
        String query = "SELECT * FROM lab_reports WHERE lab_technician_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    labReports.add(new LabReport(
                            rs.getInt("id"),
                            rs.getInt("test_request_id"),
                            rs.getInt("lab_technician_id"),
                            rs.getString("result"),
                            rs.getTimestamp("generated_date")
                    ));
                }
            }
        }
        return labReports;
    }

    public boolean saveLabReport(int testRequestId, int technicianId, String result) throws SQLException {
        String query = "INSERT INTO lab_reports (test_request_id, lab_technician_id, result) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, testRequestId);
            stmt.setInt(2, technicianId);
            stmt.setString(3, result);

            return stmt.executeUpdate() > 0;
        }
    }


    // Delete a LabReport
    public void deleteLabReport(int id) throws SQLException {
        String query = "DELETE FROM lab_reports WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
