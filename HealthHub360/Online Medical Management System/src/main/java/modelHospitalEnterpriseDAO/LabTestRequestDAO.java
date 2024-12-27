/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseDAO;

import modelHospitalEnterprise.LabTestRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ui.DatabaseUtil;

/**
 *
 * @author rdhan
 */
public class LabTestRequestDAO {
    
    private Connection connection;

    public LabTestRequestDAO() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
    }

    public boolean addLabTestRequest(LabTestRequest request) throws SQLException {
        String query = "INSERT INTO LabTestRequest (patientId, doctorId, testName, status) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, request.getPatientId());
        ps.setInt(2, request.getDoctorId());
        ps.setString(3, request.getTestName());
        ps.setString(4, "Pending");
        return ps.executeUpdate() > 0;
    }

    public List<LabTestRequest> getLabTestRequestsByPatientId(int patientId) throws SQLException {
    String query = "SELECT * FROM LabTestRequest WHERE patientId = ?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setInt(1, patientId);
    ResultSet rs = ps.executeQuery();

    List<LabTestRequest> labTestRequests = new ArrayList<>();
    while (rs.next()) {
        labTestRequests.add(new LabTestRequest(
            rs.getInt("requestId"),
            rs.getInt("patientId"),
            rs.getInt("doctorId"),
            rs.getString("testName"),
            rs.getString("status")
        ));
    }
    return labTestRequests;
}

}
