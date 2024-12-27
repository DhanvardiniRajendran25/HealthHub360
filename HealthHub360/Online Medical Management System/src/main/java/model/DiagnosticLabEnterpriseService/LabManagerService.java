/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterpriseService;

import model.DiagnosticLabEnterprise.TestRequest;
import model.DiagnosticLabEnterprise.LabTechnician;
import model.DiagnosticLabEnterpriseDAO.LabManagerDAO;
import model.DiagnosticLabEnterpriseDAO.TestRequestDAO;
import model.DiagnosticLabEnterpriseDAO.LabTechnicianDAO;
import model.DiagnosticLabEnterpriseDAO.LabTestDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.DiagnosticLabEnterprise.LabReport;
import model.DiagnosticLabEnterpriseDAO.LabReportDAO;
import ui.DatabaseUtil;

/**
 *
 * @author AnjanaSruthiR
 */
public class LabManagerService {

    private LabManagerDAO labManagerDAO;
    private TestRequestDAO testRequestDAO;
    private LabTechnicianDAO labTechnicianDAO;
    private LabTestDAO labTestDAO;

    public LabManagerService(LabManagerDAO labManagerDAO, TestRequestDAO testRequestDAO, LabTechnicianDAO labTechnicianDAO, LabTestDAO labTestDAO) {
        this.labManagerDAO = labManagerDAO;
        this.testRequestDAO = testRequestDAO;
        this.labTestDAO = labTestDAO;
        this.labTechnicianDAO = labTechnicianDAO;

    }

    // Fetch test requests for the lab manager
// In LabManagerService.java
    public List<TestRequest> getTestRequestsForManager(int managerId, String status) throws SQLException {
        return testRequestDAO.getTestRequestsForManager(managerId, status);
    }

    // Get Lab Test name
    public String getLabTestName(int labTestId) throws SQLException {
        return labTestDAO.getLabTestById(labTestId).getName();
    }

    // Get Technician name
    public String getTechnicianName(int technicianId) throws SQLException {
        LabTechnician technician = labTechnicianDAO.getTechnicianById(technicianId);
        return technician != null ? technician.getName() : "Unassigned";
    }

    // Assign a technician to a test request
    public void assignTechnician(int requestId, int technicianId) throws SQLException {
        // Validate business rules (e.g., status must be "Pending")
        TestRequest request = testRequestDAO.getTestRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Test request not found.");
        }
        validateRequestStatus(request, "Created");

        // Delegate to DAO
        labManagerDAO.assignTechnicianToRequest(requestId, technicianId);

        // Update the request status
        request.setTechnicianId(technicianId);
        request.setStatus("Assigned");
        testRequestDAO.updateTestRequest(request);
    }

    // Get all lab technicians
    public List<LabTechnician> getAllTechnicians() throws SQLException {
        return labTechnicianDAO.getAllLabTechnicians();
    }

    public void validateRequestStatus(TestRequest request, String expectedStatus) {
        if (!expectedStatus.equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException(
                    "Operation not allowed. Expected status: " + expectedStatus + ", but found: " + request.getStatus()
            );
        }
    }

    public List<LabReport> getAllLabReports() throws SQLException {
        LabReportDAO labReportDAO = new LabReportDAO(DatabaseUtil.getConnection());
        return labReportDAO.getAllLabReports();
    }

    public List<LabReport> getLabReportsByTechnician(int technicianId) throws SQLException {
        LabReportDAO labReportDAO = new LabReportDAO(DatabaseUtil.getConnection());
        return labReportDAO.getLabReportsByTechnician(technicianId);
    }

    public List<Map<String, Object>> getRequestsByStatus(int managerId, String status) {
        try {
            return testRequestDAO.getRequestsByStatus(managerId, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
