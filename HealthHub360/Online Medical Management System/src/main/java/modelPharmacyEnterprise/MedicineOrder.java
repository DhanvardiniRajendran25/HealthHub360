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
public class MedicineOrder {
      private int id;
    private int medicineId;
    private int quantity;
    private java.sql.Date orderDate;
    private String status;
    private int userId;

  // Constructor
    public MedicineOrder(int id, int medicineId, int quantity, java.sql.Date orderDate, String status, int userId) {
        this.id = id;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.status = status;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
