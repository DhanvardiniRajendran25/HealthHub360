/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseDAO;

import model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ui.DatabaseUtil;

/**
 *
 * @author rdhan
 */
public class PatientDAO {
    
    private Connection connection;

    // Constructor initializes database connection
    public PatientDAO() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
    }

    // Add a new patient
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (insurance_number, date_of_birth, medical_history, person_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, patient.getInsuranceNumber());
            stmt.setDate(2, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(3, String.join(", ", patient.getMedicalHistory().toString())); // Convert list to string
            stmt.setInt(4, patient.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve a patient by ID
    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM patients INNER JOIN persons ON patients.person_id = persons.id WHERE patients.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Patient patient = new Patient(
                    rs.getInt("person_id"),
                    rs.getString("insurance_number"),
                    rs.getDate("date_of_birth").toString(),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("contact_number")
                );
                // Retrieve additional patient details
                patient.setMedicalHistory(new ArrayList<>()); // Add logic to fetch medical records
                return patient;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve all patients
    public List<Patient> getAllPatients() {
        String sql = "SELECT * FROM patients INNER JOIN persons ON patients.person_id = persons.id";
        List<Patient> patients = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Patient patient = new Patient(
                    rs.getInt("person_id"),
                    rs.getString("insurance_number"),
                    rs.getDate("date_of_birth").toString(),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("contact_number")
                );
                // Retrieve additional patient details
                patient.setMedicalHistory(new ArrayList<>()); // Add logic to fetch medical records
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    // Update patient details
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET insurance_number = ?, date_of_birth = ?, medical_history = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, patient.getInsuranceNumber());
            stmt.setDate(2, Date.valueOf(patient.getDateOfBirth())); // Parse string to LocalDate
            stmt.setString(3, String.join(", ", patient.getMedicalHistory().toString())); // Convert list to string
            stmt.setInt(4, patient.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a patient
    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
