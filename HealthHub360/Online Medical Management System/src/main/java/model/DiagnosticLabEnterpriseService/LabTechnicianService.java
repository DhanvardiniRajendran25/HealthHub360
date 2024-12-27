/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterpriseService;
import model.DiagnosticLabEnterprise.LabTechnician;
import model.DiagnosticLabEnterprise.TestRequest;
import model.DiagnosticLabEnterpriseDAO.LabTechnicianDAO;
import model.DiagnosticLabEnterpriseDAO.TestRequestDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.DiagnosticLabEnterpriseDAO.LabReportDAO;
import ui.DatabaseUtil;

/**
 *
 * @author AnjanaSruthiR
 */

public class LabTechnicianService {
    private final LabTechnicianDAO labTechnicianDAO;
    private final TestRequestDAO testRequestDAO;
    private Connection connection;


    public LabTechnicianService(Connection connection) {
        this.connection = connection;
        this.labTechnicianDAO = new LabTechnicianDAO(connection);
        this.testRequestDAO = new TestRequestDAO(connection);
    }

    // Fetch technician details
    public LabTechnician getTechnicianDetails(int technicianId) {
        try {
            return labTechnicianDAO.getTechnicianById(technicianId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getFilteredTestRequests(int technicianId, String status) throws SQLException {
        return testRequestDAO.getFilteredTestRequests(technicianId, status);
    }

    public String getTestRequestDetails(int requestId) throws SQLException {
        return testRequestDAO.getTestRequestDetails(requestId);
    }

    // Fetch workload for the technician
    public int getTechnicianWorkload(int technicianId) {
        try {
            return labTechnicianDAO.getTechnicianWorkloadFiltered(technicianId, null).getOrDefault(technicianId, 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Fetch test requests assigned to the technician
    public List<Map<String, Object>> getTestRequestsByTechnicianId(int technicianId) throws SQLException {
        return testRequestDAO.getRequestsByTechnicianId(technicianId);
    }
    
    public boolean updateTestRequestStatus(int requestId, String newStatus) throws SQLException {
        return testRequestDAO.updateRequestStatus(requestId, newStatus);
    }

    public boolean saveLabReport(int testRequestId, int technicianId, String result) throws SQLException {
        LabReportDAO labReportDAO = new LabReportDAO(connection);
        return labReportDAO.saveLabReport(testRequestId, technicianId, result);
    }

    public Map<String, Object> getTestRequestDetailsById(int requestId) throws SQLException {
        LabTechnicianDAO labTechnicianDAO = new LabTechnicianDAO(connection);
        return labTechnicianDAO.fetchTestRequestDetailsById(requestId);
    }

    public List<Map<String, Object>> getAllLabReports() throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection()) {
            return labTechnicianDAO.getAllLabReports(connection);
        }
    }

    public String getLabReportResultById(int reportId) throws SQLException {
        return labTechnicianDAO.getLabReportResultById(reportId);
    }

    public List<Map<String, Object>> getLabReportsFilteredByDate(String filter) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection()) {
            return labTechnicianDAO.getLabReportsFilteredByDate(connection, filter);
        }
    }

    public List<Map<String, Object>> getNewRequestsForTechnician(int technicianId) {
        try {
            return testRequestDAO.getNewRequestsForTechnician(technicianId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public boolean updateLabReport(int reportId, String newResult) throws SQLException {
        LabReportDAO labReportDAO = new LabReportDAO(connection);
        return labReportDAO.updateLabReport(reportId, newResult);
    }
}
