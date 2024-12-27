/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterprise;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author keerthichandrakanth
 */
public class Medicine {
   private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantityInStock;
    private int reorderLevel;
    private String supplier;
    private Date expiryDate;

    // Constructor
    public Medicine(int id, String name, String description, BigDecimal price, int quantityInStock, int reorderLevel, String supplier, Date expiryDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.supplier = supplier;
        this.expiryDate = expiryDate;

    }
    
       public Medicine(String name, int quantityInStock) {
        this.name = name;
        this.quantityInStock = quantityInStock;
    }

  
    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    } 
     @Override
    public String toString() {
        return name;  // Return the medicine name to be displayed
    }
}
