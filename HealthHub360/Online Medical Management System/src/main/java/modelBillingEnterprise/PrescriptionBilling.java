/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelBillingEnterprise;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionBilling {
    private int prescriptionId;
    private int medicineId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private LocalDate billDate;

    public PrescriptionBilling(int prescriptionId, int medicineId, int quantity, BigDecimal price, BigDecimal totalAmount, LocalDate billDate) {
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
    }

    // Getters and Setters
    public int getPrescriptionId() {
        return prescriptionId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDate getBillDate() {
        return billDate;
    }
    
    
}
