/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseDAO;

import model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ui.DatabaseUtil;

/**
 *
 * @author rdhan
 */
public class DoctorDAO {
    
    public void addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, contactNumber, email, address, specialization) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters
            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getContactNumber());
            stmt.setString(3, doctor.getEmail());
            stmt.setString(4, doctor.getAddress());
            stmt.setString(5, doctor.getSpecialization());

            // Execute the statement
            stmt.executeUpdate();

            // Retrieve and set the generated ID
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                doctor.setId(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error adding doctor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a doctor by their ID.
     *
     * @param doctorId the ID of the doctor to retrieve.
     * @return the Doctor object, or null if not found.
     */
    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contactNumber"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("specialization")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public Doctor getDoctorByName(String doctorName) throws SQLException {
    try (Connection conn = DatabaseUtil.getConnection()) {
        String query = "SELECT * FROM doctors WHERE name = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, doctorName);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Doctor(
                          rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contactNumber"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("specialization")
            );
        }
    }
    return null;
}



    /**
     * Retrieves all doctors from the database.
     *
     * @return a list of all Doctor objects.
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contactNumber"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("specialization")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }
    
}
