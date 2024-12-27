/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;
import modelHospitalEnterprise.Schedule;

/**
 *
 * @author rdhan
 */

public class Doctor extends Person {
    private String specialization;
    private List<Schedule> schedule;
    
    public Doctor(int id, String name, String contactNumber, String email, String address, String specialization) {
        super(id, name, contactNumber, email, address); // Call Person's constructor
        this.specialization = specialization;
        this.schedule = new ArrayList<>();
    }

    // Constructor
    public Doctor(int id, int personId, String specialization) {
        super(id, "", "", "", "");  // Call Person's constructor
        this.specialization = specialization;
        this.schedule = new ArrayList<>();
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public List<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
    }
    
    public void setId(int id) {
        super.setId(id); // Set the ID using the Person class's setter
    }
}
