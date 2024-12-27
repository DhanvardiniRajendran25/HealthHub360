/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelHospitalEnterpriseService;

import model.Doctor;
import modelHospitalEnterprise.Schedule;

import java.util.ArrayList;
import java.util.List;
import modelHospitalEnterpriseDAO.DoctorDAO;

/**
 *
 * @author rdhan
 */
public class DoctorService {
    
    private List<Doctor> doctors;

    public DoctorService() {
        this.doctors = new ArrayList<>();
    }

    // Add a new doctor
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    // Get a doctor by ID
    public Doctor getDoctorById(int id) {
        return doctors.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }
    
    public Doctor getDoctorByName(String doctorName) {
    try {
        DoctorDAO doctorDAO = new DoctorDAO();
        return doctorDAO.getDoctorByName(doctorName); // Query database
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}


    // Update doctor schedule
    public void updateSchedule(int doctorId, List<Schedule> newSchedule) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor != null) {
            doctor.setSchedule(newSchedule);
        }
    }

    // Get all doctors
    public List<Doctor> getAllDoctors() {
    try {
        DoctorDAO doctorDAO = new DoctorDAO();
        return doctorDAO.getAllDoctors();
    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}

    
}
