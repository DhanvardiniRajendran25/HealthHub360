/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.InsuranceEnterpriseDAO;

import model.InsuranceEnterprise.InsurancePolicy;
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
public class InsurancePolicyDAO {

    private Connection connection;

    public InsurancePolicyDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new InsurancePolicy
    public boolean addPolicy(String name, double coverageAmount, double premium, String description) throws SQLException {
        String query = "INSERT INTO insurance_policies (name, coverage_amount, premium, description) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setDouble(2, coverageAmount);
            stmt.setDouble(3, premium);
            stmt.setString(4, description);
            return stmt.executeUpdate() > 0;
        }
    }

    // Get an InsurancePolicy by ID
    public InsurancePolicy getInsurancePolicyById(int id) throws SQLException {
        String query = "SELECT * FROM insurance_policies WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new InsurancePolicy(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("coverage_amount"),
                        rs.getDouble("premium")
                );
            }
        }
        return null;
    }
 
    public Map<String, Object> getPolicyById(int policyId) throws SQLException {
        String query = "SELECT * FROM insurance_policies WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, policyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> policy = new HashMap<>();
                policy.put("PolicyID", rs.getInt("id"));
                policy.put("PolicyName", rs.getString("name"));
                policy.put("CoverageAmount", rs.getDouble("coverage_amount"));
                policy.put("Premium", rs.getDouble("premium"));
                policy.put("Description", rs.getString("description"));
                return policy;
            }
            return null;
        }
    }

    // Get all InsurancePoliciesByID
    public List<Map<String, Object>> getPoliciesByManagerId(int managerId) throws SQLException {
        String query = "SELECT DISTINCT ip.id AS PolicyID, ip.name AS PolicyName, "
                + "ip.coverage_amount AS CoverageAmount, ip.premium AS Premium, "
                + "ip.description AS Description "
                + "FROM insurance_policies ip "
                + "JOIN insurance_claims ic ON ip.id = ic.policy_id "
                + "WHERE ic.assigned_manager_id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, managerId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> policies = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> policy = new HashMap<>();
                    policy.put("PolicyID", rs.getInt("PolicyID"));
                    policy.put("PolicyName", rs.getString("PolicyName"));
                    policy.put("CoverageAmount", rs.getBigDecimal("CoverageAmount"));
                    policy.put("Premium", rs.getBigDecimal("Premium"));
                    policy.put("Description", rs.getString("Description"));
                    policies.add(policy);
                }
                return policies;
            }
        }
    }

    // Update an InsurancePolicy
    public boolean updatePolicy(int id, String name, double coverageAmount, double premium, String description) throws SQLException {
        String query = "UPDATE insurance_policies SET name = ?, coverage_amount = ?, premium = ?, description = ? WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setDouble(2, coverageAmount);
            stmt.setDouble(3, premium);
            stmt.setString(4, description);
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete an InsurancePolicy
    public boolean deletePolicy(int id) throws SQLException {
        String query = "DELETE FROM insurance_policies WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Get all InsurancePolicies
    public List<Map<String, Object>> getAllPolicies() throws SQLException {
        String query = "SELECT id AS PolicyID, name AS PolicyName, coverage_amount AS CoverageAmount, premium AS Premium, description AS Description FROM insurance_policies";
        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            List<Map<String, Object>> policies = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> policy = new HashMap<>();
                policy.put("PolicyID", rs.getInt("PolicyID"));
                policy.put("PolicyName", rs.getString("PolicyName"));
                policy.put("CoverageAmount", rs.getBigDecimal("CoverageAmount"));
                policy.put("Premium", rs.getBigDecimal("Premium"));
                policy.put("Description", rs.getString("Description"));
                policies.add(policy);
            }
            return policies;
        }
    }
}
