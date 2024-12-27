/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DiagnosticLabEnterpriseDAO;
import model.DiagnosticLabEnterprise.LabTest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AnjanaSruthiR
 */

public class LabTestDAO {
    private Connection connection;

    public LabTestDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new LabTest
    public void createLabTest(LabTest labTest) throws SQLException {
        String query = "INSERT INTO lab_tests (name, description, cost) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, labTest.getName());
            stmt.setString(2, labTest.getDescription());
            stmt.setDouble(3, labTest.getCost());
            stmt.executeUpdate();
        }
    }

    // Get a LabTest by ID
    public LabTest getLabTestById(int labTestId) throws SQLException {
        String query = "SELECT * FROM lab_tests WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, labTestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new LabTest(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("cost")
                );
            }
        }
        return null;
    }

    // Get all LabTests
    public List<LabTest> getAllLabTests() throws SQLException {
        List<LabTest> labTests = new ArrayList<>();
        String query = "SELECT * FROM lab_tests";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                labTests.add(new LabTest(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getDouble("cost")));
            }
        }
        return labTests;
    }

    // Update a LabTest
    public void updateLabTest(LabTest labTest) throws SQLException {
        String query = "UPDATE lab_tests SET name = ?, description = ?, cost = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, labTest.getName());
            stmt.setString(2, labTest.getDescription());
            stmt.setDouble(3, labTest.getCost());
            stmt.setInt(4, labTest.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a LabTest
    public void deleteLabTest(int id) throws SQLException {
        String query = "DELETE FROM lab_tests WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
