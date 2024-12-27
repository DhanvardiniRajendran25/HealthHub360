/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseDAO;

import modelHospitalEnterprise.MedicalRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ui.DatabaseUtil;

/**
 *
 * @author rdhan
 */
public class MedicalRecordDAO {
    
    private Connection connection;

    // Constructor initializes database connection
    public MedicalRecordDAO() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
    }

    // Add a new medical record
    public boolean addMedicalRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (patient_id, appointment_id, diagnosis, treatment, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, record.getPatientId());
            stmt.setInt(2, record.getAppointmentId());
            stmt.setString(3, record.getDiagnosis());
            stmt.setString(4, record.getTreatment());
            stmt.setString(5, record.getNotes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all medical records for a specific patient
    public List<MedicalRecord> getMedicalRecordsByPatientId(int patientId) {
        String sql = "SELECT * FROM medical_records WHERE patient_id = ?";
        List<MedicalRecord> records = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(new MedicalRecord(
                    rs.getInt("id"),
                    rs.getInt("patient_id"),
                    rs.getInt("appointment_id"),
                    rs.getString("diagnosis"),
                    rs.getString("treatment"),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
}
