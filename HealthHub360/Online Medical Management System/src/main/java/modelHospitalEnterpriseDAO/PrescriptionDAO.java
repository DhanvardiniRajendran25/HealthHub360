/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseDAO;

import modelHospitalEnterprise.Prescription;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ui.DatabaseUtil;

/**
 *
 * @author rdhan
 */
public class PrescriptionDAO {
    
    private Connection connection;

    public PrescriptionDAO() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
    }

    public boolean addPrescription(Prescription prescription) throws SQLException {
        String query = "INSERT INTO Prescription (patientId, doctorId, medication, dosage, instructions) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, prescription.getPatientId());
        ps.setInt(2, prescription.getDoctorId());
        ps.setString(3, prescription.getMedication());
        ps.setString(4, prescription.getDosage());
        ps.setString(5, prescription.getInstructions());
        return ps.executeUpdate() > 0;
    }

    public List<Prescription> getPrescriptionsByPatient(int patientId) throws SQLException {
        String query = "SELECT * FROM Prescription WHERE patientId = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        List<Prescription> prescriptions = new ArrayList<>();
        while (rs.next()) {
            prescriptions.add(new Prescription(
                rs.getInt("prescriptionId"),
                rs.getInt("patientId"),
                rs.getInt("doctorId"),
                rs.getString("medication"),
                rs.getString("dosage"),
                rs.getString("instructions")
            ));
        }
        return prescriptions;
    }
    
    public List<Prescription> getAllPrescriptions() throws SQLException {
    String query = "SELECT * FROM Prescription";
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery(query);

    List<Prescription> prescriptions = new ArrayList<>();
    while (rs.next()) {
        prescriptions.add(new Prescription(
            rs.getInt("prescriptionId"),
            rs.getInt("patientId"),
            rs.getInt("doctorId"),
            rs.getString("medication"),
            rs.getString("dosage"),
            rs.getString("instructions")
        ));
    }
    return prescriptions;
}

    
}
