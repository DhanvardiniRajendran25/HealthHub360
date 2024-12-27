/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.InsuranceEnterpriseDAO;

import model.InsuranceEnterprise.InsuranceClaim;
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
public class InsuranceClaimDAO {

    private Connection connection;

    public InsuranceClaimDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new InsuranceClaim
    public void createInsuranceClaim(InsuranceClaim claim) throws SQLException {
        String query = "INSERT INTO insurance_claims (patient_id, policy_id, claim_amount, approved_amount, status, "
                + "remarks, assigned_manager_id, claim_date, processing_time_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, claim.getPatientId());
            stmt.setInt(2, claim.getPolicyId());
            stmt.setDouble(3, claim.getClaimAmount());
            stmt.setDouble(4, claim.getApprovedAmount());
            stmt.setString(5, claim.getStatus());
            stmt.setString(6, claim.getRemarks());
            stmt.setInt(7, claim.getAssignedManagerId());
            stmt.setTimestamp(8, new Timestamp(claim.getClaimDate().getTime()));
            stmt.setInt(9, claim.getProcessingTimeDays());
            stmt.executeUpdate();
        }
    }

    // Get an InsuranceClaim by ID
    public InsuranceClaim getInsuranceClaimById(int id) throws SQLException {
        String query = "SELECT * FROM insurance_claims WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new InsuranceClaim(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("policy_id"),
                        rs.getDouble("claim_amount"),
                        rs.getDouble("approved_amount"),
                        rs.getString("status"),
                        rs.getString("remarks"),
                        rs.getInt("assigned_manager_id"),
                        rs.getTimestamp("claim_date"),
                        rs.getInt("processing_time_days")
                );
            }
        }
        return null;
    }

    // Get all InsuranceClaims
    public List<InsuranceClaim> getAllInsuranceClaims() throws SQLException {
        List<InsuranceClaim> claims = new ArrayList<>();
        String query = "SELECT * FROM insurance_claims";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                claims.add(new InsuranceClaim(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("policy_id"),
                        rs.getDouble("claim_amount"),
                        rs.getDouble("approved_amount"),
                        rs.getString("status"),
                        rs.getString("remarks"),
                        rs.getInt("assigned_manager_id"),
                        rs.getTimestamp("claim_date"),
                        rs.getInt("processing_time_days")
                ));
            }
        }
        return claims;
    }

    // Update an InsuranceClaim
    public void updateInsuranceClaim(InsuranceClaim claim) throws SQLException {
        String query = "UPDATE insurance_claims SET patient_id = ?, policy_id = ?, claim_amount = ?, approved_amount = ?, "
                + "status = ?, remarks = ?, assigned_manager_id = ?, claim_date = ?, processing_time_days = ? WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, claim.getPatientId());
            stmt.setInt(2, claim.getPolicyId());
            stmt.setDouble(3, claim.getClaimAmount());
            stmt.setDouble(4, claim.getApprovedAmount());
            stmt.setString(5, claim.getStatus());
            stmt.setString(6, claim.getRemarks());
            stmt.setInt(7, claim.getAssignedManagerId());
            stmt.setTimestamp(8, new Timestamp(claim.getClaimDate().getTime()));
            stmt.setInt(9, claim.getProcessingTimeDays());
            stmt.setInt(10, claim.getId());
            stmt.executeUpdate();
        }
    }

    public boolean updateClaimStatus(int claimId, String newStatus, String remarks) throws SQLException {
        String query = "UPDATE insurance_claims SET status = ?, remarks = ? WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, remarks);
            stmt.setInt(3, claimId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete an InsuranceClaim
    public void deleteInsuranceClaim(int id) throws SQLException {
        String query = "DELETE FROM insurance_claims WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getClaimsByManagerId(int managerId) throws SQLException {
        String query = "SELECT ic.id AS ClaimID, p.name AS PatientName, ip.name AS PolicyName, "
                + "ic.claim_date AS ClaimDate, ic.claim_amount AS ClaimAmount, "
                + "ic.status AS Status, ic.remarks AS Remarks "
                + "FROM insurance_claims ic "
                + "JOIN patients pt ON ic.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "JOIN insurance_policies ip ON ic.policy_id = ip.id "
                + "WHERE ic.assigned_manager_id = ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, managerId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> claims = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> claim = new HashMap<>();
                    claim.put("ClaimID", rs.getInt("ClaimID"));
                    claim.put("PatientName", rs.getString("PatientName"));
                    claim.put("PolicyName", rs.getString("PolicyName"));
                    claim.put("ClaimDate", rs.getDate("ClaimDate"));
                    claim.put("ClaimAmount", rs.getBigDecimal("ClaimAmount"));
                    claim.put("Status", rs.getString("Status"));
                    claim.put("Remarks", rs.getString("Remarks"));
                    claims.add(claim);
                }
                return claims;
            }
        }
    }

    public List<Map<String, Object>> getFilteredClaims(int managerId, String status, String dateRange) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ic.id AS ClaimID, p.name AS PatientName, ip.name AS PolicyName, "
                + "ic.claim_date AS ClaimDate, ic.claim_amount AS ClaimAmount, "
                + "ic.status AS Status, ic.remarks AS Remarks "
                + "FROM insurance_claims ic "
                + "JOIN insurance_policies ip ON ic.policy_id = ip.id "
                + "JOIN patients pt ON ic.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "WHERE ic.assigned_manager_id = ? ");

        if (!"All".equalsIgnoreCase(status)) {
            query.append("AND ic.status = ? ");
        }

        if (!"All".equalsIgnoreCase(dateRange)) {
            switch (dateRange) {
                case "Today":
                    query.append("AND CAST(ic.claim_date AS DATE) = CAST(GETDATE() AS DATE) ");
                    break;
                case "Last 7 Days":
                    query.append("AND ic.claim_date >= DATEADD(DAY, -7, GETDATE()) ");
                    break;
                case "Last 30 Days":
                    query.append("AND ic.claim_date >= DATEADD(DAY, -30, GETDATE()) ");
                    break;
            }
        }

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(query.toString())) {
            ps.setInt(1, managerId);

            int paramIndex = 2;
            if (!"All".equalsIgnoreCase(status)) {
                ps.setString(paramIndex++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> claims = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> claim = new HashMap<>();
                    claim.put("ClaimID", rs.getInt("ClaimID"));
                    claim.put("PatientName", rs.getString("PatientName"));
                    claim.put("PolicyName", rs.getString("PolicyName"));
                    claim.put("ClaimDate", rs.getDate("ClaimDate"));
                    claim.put("ClaimAmount", rs.getBigDecimal("ClaimAmount"));
                    claim.put("Status", rs.getString("Status"));
                    claim.put("Remarks", rs.getString("Remarks"));
                    claims.add(claim);
                }
                return claims;
            }
        }
    }
}
