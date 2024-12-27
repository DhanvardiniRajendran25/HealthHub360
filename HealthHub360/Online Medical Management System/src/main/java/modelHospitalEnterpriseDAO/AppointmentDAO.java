/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelHospitalEnterprise.Appointment;
import ui.DatabaseUtil;

/**
 *
 * @author rdhan
 */
public class AppointmentDAO {
    
    private Connection connection;

    public AppointmentDAO() throws SQLException {
        this.connection = DatabaseUtil.getConnection();
    }
    
    public List<Appointment> getAllAppointments() {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.id, p1.name AS patient_name, a.date, a.status " +
                 "FROM appointments a " +
                 "JOIN patients p ON a.patient_id = p.id " +
                 "JOIN persons p1 ON p.person_id = p1.id";
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            appointments.add(new Appointment(
                rs.getInt("id"),
                rs.getString("patient_name"),
                rs.getString("date"),
                rs.getString("status")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return appointments;
}


    // Get all available appointments
    public List<Appointment> getAvailableAppointments() throws SQLException {
        String query = "SELECT * FROM appointments WHERE status = 'Available'";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        List<Appointment> appointments = new ArrayList<>();
        while (rs.next()) {
            appointments.add(new Appointment(
                rs.getInt("appointmentId"),
                rs.getString("date"), // Replace with LocalDate if Appointment class changes
                rs.getString("time"), // Replace with LocalTime if Appointment class changes
                rs.getString("status")
            ));
        }
        return appointments;
    }

    // Add a new appointment
    public boolean addAppointment(Appointment appointment) throws SQLException {
    String query = "INSERT INTO appointments (patientId, doctorId, date, time, status) VALUES (?, ?, ?, ?, ?)";
    PreparedStatement ps = connection.prepareStatement(query);

    String[] dateTimeParts = appointment.getDateTime().split(" ");
    if (dateTimeParts.length < 2) {
        throw new SQLException("Invalid dateTime format. Expected 'yyyy-MM-dd HH:mm:ss'.");
    }

    ps.setInt(1, appointment.getPatientId());
    ps.setInt(2, appointment.getDoctorId());
    ps.setString(3, dateTimeParts[0]); // Date
    ps.setString(4, dateTimeParts[1]); // Time
    ps.setString(5, appointment.getStatus());
    return ps.executeUpdate() > 0;
}

public boolean updateAppointmentStatus(int appointmentId, String status) throws SQLException {
    String query = "UPDATE appointments SET status = ? WHERE appointmentId = ?";
    PreparedStatement ps = connection.prepareStatement(query);
    ps.setString(1, status);
    ps.setInt(2, appointmentId);
    return ps.executeUpdate() > 0;
}




    // Approve an appointment
    public boolean approveAppointment(int appointmentId) throws SQLException {
        String query = "UPDATE appointments SET status = 'Approved' WHERE appointmentId = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, appointmentId);
        return ps.executeUpdate() > 0;
    }
    
}
