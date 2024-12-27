/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseService;

import modelHospitalEnterprise.Appointment;

import java.util.ArrayList;
import java.util.List;
import model.Doctor;
import modelHospitalEnterpriseDAO.AppointmentDAO;

/**
 *
 * @author rdhan
 */
public class AppointmentService {
    
    private List<Appointment> appointments;
    private DoctorService doctorService;

    public AppointmentService() {
        this.appointments = new ArrayList<>();
        this.doctorService = new DoctorService(); // Dependency injection
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
    try {
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        return appointmentDAO.updateAppointmentStatus(appointmentId, status);
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean bookAppointment(String doctorName, String dateTime, String details) {
    try {
        Doctor doctor = doctorService.getDoctorByName(doctorName);
        if (doctor == null) {
            throw new Exception("Doctor not found: " + doctorName);
        }

        Appointment appointment = new Appointment();
        appointment.setDoctorId(doctor.getId());
        appointment.setDateTime(dateTime);
        appointment.setStatus("Pending");

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        return appointmentDAO.addAppointment(appointment);
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    public List<Appointment> getAllAppointments() {
        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            return appointmentDAO.getAvailableAppointments();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
}
