/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterprise;

import java.sql.Date;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionAssignment {
       private int id;
    private int prescriptionId;
    private int pharmacistId;
    private Date assignedDate;
    private Date dateFulfilled;
    private String status;

    // Constructor
    public PrescriptionAssignment(int id, int prescriptionId, int pharmacistId, Date assignedDate, Date dateFulfilled, String status) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.pharmacistId = pharmacistId;
        this.assignedDate = assignedDate;
        this.dateFulfilled = dateFulfilled;
        this.status = status;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(int pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Date getDateFulfilled() {
        return dateFulfilled;
    }

    public void setDateFulfilled(Date dateFulfilled) {
        this.dateFulfilled = dateFulfilled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
