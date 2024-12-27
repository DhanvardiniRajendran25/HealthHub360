/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterpriseDAO;

import model.DiagnosticLabEnterprise.LabTechnician;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ui.DatabaseUtil;

/**
 *
 * @author AnjanaSruthiR
 */
public class LabTechnicianDAO {

    private Connection connection;

    public LabTechnicianDAO(Connection connection) {
        this.connection = connection;
    }

    // Fetch LabTechnician by ID
    public LabTechnician getTechnicianById(int technicianId) throws SQLException {
        String query = "SELECT * FROM users u "
                + "JOIN persons p ON u.person_id = p.id "
                + "JOIN roles r ON u.role_id = r.id "
                + "WHERE u.id = ? AND r.role_name = 'Lab Technician'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new LabTechnician(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        "General Certification", // Placeholder certification
                        "Morning", // Placeholder shift
                        "General", // Placeholder specialization
                        3 // Placeholder experience
                );
            }
        }
        return null;
    }

    public List<LabTechnician> getAllLabTechnicians() throws SQLException {
        List<LabTechnician> technicians = new ArrayList<>();
        String query = "SELECT * FROM users u "
                + "JOIN persons p ON u.person_id = p.id "
                + "JOIN roles r ON u.role_id = r.id "
                + "WHERE r.role_name = 'Lab Technician'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                technicians.add(new LabTechnician(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        "General Certification", // Placeholder
                        "Morning", // Placeholder
                        "General", // Placeholder specialization
                        3 // Placeholder for experience
                ));
            }
        }
        return technicians;
    }

    public Map<Integer, Integer> getTechnicianWorkloadFiltered(Integer technicianId, String status) throws SQLException {
        StringBuilder query = new StringBuilder(
                "SELECT technician_id, COUNT(*) AS Workload FROM test_requests WHERE 1=1"
        );

        if (technicianId != null) {
            query.append(" AND technician_id = ?");
        }
        if (status != null) {
            query.append(" AND status = ?");
        }
        query.append(" GROUP BY technician_id");

        Map<Integer, Integer> workloadMap = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int index = 1;
            if (technicianId != null) {
                stmt.setInt(index++, technicianId);
            }
            if (status != null) {
                stmt.setString(index++, status);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    workloadMap.put(rs.getInt("technician_id"), rs.getInt("Workload"));
                }
            }
        }
        return workloadMap;
    }

    public Map<String, Object> fetchTestRequestDetailsById(int requestId) throws SQLException {
        String query = "SELECT tr.id AS RequestID, lt.name AS TestType, per.name AS PatientName "
                + "FROM test_requests tr "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN patients p ON tr.patient_id = p.id "
                + "JOIN persons per ON p.person_id = per.id "
                + "WHERE tr.id = ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> details = new HashMap<>();
                    details.put("RequestID", rs.getInt("RequestID"));
                    details.put("TestType", rs.getString("TestType"));
                    details.put("PatientName", rs.getString("PatientName"));
                    return details;
                } else {
                    throw new SQLException("No details found for the given request ID.");
                }
            }
        }
    }

    public List<Map<String, Object>> getAllLabReports(Connection connection) throws SQLException {
        String query = "SELECT lr.id AS ReportID, tr.id AS RequestID, lt.name AS TestType, "
                + "p.name AS PatientName, t.name AS TechnicianName, lr.generated_date AS GeneratedDate "
                + "FROM lab_reports lr "
                + "JOIN test_requests tr ON lr.test_request_id = tr.id "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN patients pt ON tr.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "JOIN users u ON lr.lab_technician_id = u.id "
                + "JOIN persons t ON u.person_id = t.id";

        List<Map<String, Object>> labReports = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> report = new HashMap<>();
                report.put("ReportID", rs.getInt("ReportID"));
                report.put("RequestID", rs.getInt("RequestID"));
                report.put("TestType", rs.getString("TestType"));
                report.put("PatientName", rs.getString("PatientName"));
                report.put("TechnicianName", rs.getString("TechnicianName"));
                report.put("GeneratedDate", rs.getDate("GeneratedDate"));
                labReports.add(report);
            }
        }
        return labReports;
    }

    public String getLabReportResultById(int reportId) throws SQLException {
        String query = "SELECT result FROM lab_reports WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); // Ensure fresh connection
                 PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("result");
                } else {
                    throw new SQLException("Lab report not found for ID: " + reportId);
                }
            }
        }
    }

    public List<Map<String, Object>> getLabReportsFilteredByDate(Connection connection, String filter) throws SQLException {
        String query = "SELECT lr.id AS ReportID, tr.id AS RequestID, lt.name AS TestType, "
                + "p.name AS PatientName, t.name AS TechnicianName, lr.generated_date AS GeneratedDate "
                + "FROM lab_reports lr "
                + "JOIN test_requests tr ON lr.test_request_id = tr.id "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN patients pt ON tr.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "JOIN users u ON lr.lab_technician_id = u.id "
                + "JOIN persons t ON u.person_id = t.id ";

        String dateCondition = "";

        switch (filter) {
            case "Today":
                // Filter for today's reports
                dateCondition = "WHERE lr.generated_date >= DATEADD(DAY, -1, CAST(SYSDATETIME() AS DATE)) "
                        + "AND lr.generated_date < CAST(SYSDATETIME() AS DATE)";
                break;
            case "Last 7 Days":
                // Filter for reports in the last 7 days
                dateCondition = "WHERE lr.generated_date >= DATEADD(DAY, -7, SYSDATETIME())";
                break;
            case "Last 30 Days":
                // Filter for reports in the last 30 days
                dateCondition = "WHERE lr.generated_date >= DATEADD(DAY, -30, SYSDATETIME())";
                break;
            case "Last 3 Months":
                // Filter for reports in the last 3 months
                dateCondition = "WHERE lr.generated_date >= DATEADD(MONTH, -3, SYSDATETIME())";
                break;
            case "Last 1 Year":
                // Filter for reports in the last year
                dateCondition = "WHERE lr.generated_date >= DATEADD(YEAR, -1, SYSDATETIME())";
                break;
            default:
                // No condition for "All" (fetch all reports)
                break;
        }

        query += dateCondition;

        List<Map<String, Object>> labReports = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> report = new HashMap<>();
                report.put("ReportID", rs.getInt("ReportID"));
                report.put("RequestID", rs.getInt("RequestID"));
                report.put("TestType", rs.getString("TestType"));
                report.put("PatientName", rs.getString("PatientName"));
                report.put("TechnicianName", rs.getString("TechnicianName"));
                report.put("GeneratedDate", rs.getTimestamp("GeneratedDate")); // Use Timestamp for accurate time
                labReports.add(report);
            }
        }
        return labReports;
    }

}
