/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterprise;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionMedicine {
    private int id;
    private int prescriptionId;
    private int medicineId;
    private int quantity;

    // Constructor
    public PrescriptionMedicine( int prescriptionId, int medicineId, int quantity) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.quantity = quantity;
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

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
}
