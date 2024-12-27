/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseService;
import modelPharmacyEnterpriseDAO.MedicineOrderDAO;
import java.sql.Date;
import java.util.List;
import modelPharmacyEnterprise.MedicineOrder;

/**
 *
 * @author keerthichandrakanth
 */
public class MedicineOrderService {
    
    private final MedicineOrderDAO medicineOrderDAO;

    public MedicineOrderService() {
        this.medicineOrderDAO = new MedicineOrderDAO();
    }

    /**
     * Adds a new medicine order with validation.
     */
 public void placeOrder(int medicineId, int quantity, Date orderDate, String status, int userId) {
    if (medicineId <= 0 || quantity <= 0) {
        throw new IllegalArgumentException("Invalid medicine ID or quantity.");
    }
    if (status == null || status.isEmpty()) {
        throw new IllegalArgumentException("Order status cannot be null or empty.");
    }
    if (userId <= 0) {
        throw new IllegalArgumentException("Invalid user ID.");
    }

    // Convert java.util.Date to java.sql.Date
    java.sql.Date sqlOrderDate = new java.sql.Date(orderDate.getTime());

    // Create a new MedicineOrder instance
    MedicineOrder order = new MedicineOrder(0, medicineId, quantity, sqlOrderDate, status, userId);

    // Save the order to the database
    medicineOrderDAO.create(order);
}
 
  public List<MedicineOrder> getOrdersForUser(int userId) {
        return medicineOrderDAO.getOrdersForUser(userId);
    }
  
  
  public void updateOrderStatusToReceived(int orderId) {
        medicineOrderDAO.updateOrderStatusToReceived(orderId);
    }
  
}
