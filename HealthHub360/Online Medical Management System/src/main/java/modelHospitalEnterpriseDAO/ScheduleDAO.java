/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseDAO;

import java.sql.*;
import java.util.*;
import modelHospitalEnterprise.Schedule;

/**
 *
 * @author rdhan
 */
public class ScheduleDAO {
    
    private Connection connection;

    public ScheduleDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Schedule> getSchedulesByDoctor(int doctorId) throws SQLException {
        String query = "SELECT * FROM Schedule WHERE doctorId = ? AND isAvailable = true";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();

        List<Schedule> schedules = new ArrayList<>();
        while (rs.next()) {
            schedules.add(new Schedule(
                rs.getInt("scheduleId"),
                rs.getInt("doctorId"),
                rs.getString("date"),
                rs.getString("startTime"),
                rs.getString("endTime"),
                rs.getBoolean("isAvailable")
            ));
        }
        return schedules;
    }

    public boolean addSchedule(Schedule schedule) throws SQLException {
        String query = "INSERT INTO Schedule (doctorId, date, startTime, endTime, isAvailable) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, schedule.getDoctorId());
        ps.setString(2, schedule.getDate());
        ps.setString(3, schedule.getStartTime());
        ps.setString(4, schedule.getEndTime());
        ps.setBoolean(5, schedule.isAvailable());
        return ps.executeUpdate() > 0;
    }

    public boolean deleteSchedule(int scheduleId) throws SQLException {
        String query = "DELETE FROM Schedule WHERE scheduleId = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, scheduleId);
        return ps.executeUpdate() > 0;
    }
    
}
