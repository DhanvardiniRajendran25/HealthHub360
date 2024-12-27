/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.InsuranceEnterpriseService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import model.InsuranceEnterpriseDAO.InsuranceClaimDAO;
import model.InsuranceEnterpriseDAO.InsurancePolicyDAO;

/**
 *
 * @author AnjanaSruthiR
 */
public class InsuranceManagerService {

    private InsurancePolicyDAO insurancePolicyDAO;
    private InsuranceClaimDAO insuranceClaimDAO;
    private int loggedInManagerId;
    private Connection connection; 

    public InsuranceManagerService(Connection connection, int managerId) {
        this.insurancePolicyDAO = new InsurancePolicyDAO(connection);
        this.insuranceClaimDAO = new InsuranceClaimDAO(connection);
        this.connection = connection;
        this.loggedInManagerId = managerId;
    }

    public int getLoggedInManagerId() {
        return loggedInManagerId;
    }
    
    public Map<String, Object> getPolicyById(int policyId) throws SQLException {
        return insurancePolicyDAO.getPolicyById(policyId);
    }

    public List<Map<String, Object>> getPoliciesByManagerId(int managerId) throws SQLException {
        return insurancePolicyDAO.getPoliciesByManagerId(managerId);
    }

    public List<Map<String, Object>> getAllPolicies() throws SQLException {
        return insurancePolicyDAO.getAllPolicies();
    }

    public List<Map<String, Object>> getClaimsByManagerId(int managerId) throws SQLException {
        return insuranceClaimDAO.getClaimsByManagerId(managerId);
    }

    public List<Map<String, Object>> getFilteredClaims(int managerId, String status, String dateFilter) throws SQLException {
        return insuranceClaimDAO.getFilteredClaims(managerId, status, dateFilter);
    }

    public boolean updateClaimStatus(int claimId, String newStatus, String remarks) throws SQLException {
        return insuranceClaimDAO.updateClaimStatus(claimId, newStatus, remarks);
    }

    public boolean addPolicy(String name, double coverageAmount, double premium, String description) throws SQLException {
        return insurancePolicyDAO.addPolicy(name, coverageAmount, premium, description);
    }

    public boolean updatePolicy(int id, String name, double coverageAmount, double premium, String description) throws SQLException {
        return insurancePolicyDAO.updatePolicy(id, name, coverageAmount, premium, description);
    }
    
    public Connection getConnection() {
        return this.connection;
    }
}
