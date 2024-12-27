/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelPharmacyEnterprise.MedicineOrder;
import ui.DatabaseUtil;

/**
 *
 * @author keerthichandrakanth
 */
public class MedicineOrderDAO {
    
    public void create(MedicineOrder order) {
    String query = "INSERT INTO medicine_orders (medicine_id, quantity, order_date, status, user_id) " +
                   "VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, order.getMedicineId());
        stmt.setInt(2, order.getQuantity());
        stmt.setDate(3, order.getOrderDate());
        stmt.setString(4, order.getStatus());
        stmt.setInt(5, order.getUserId()); // Include user_id in the query

        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
     public List<MedicineOrder> getOrdersForUser(int userId) {
        List<MedicineOrder> orders = new ArrayList<>();
        String query = "SELECT * FROM medicine_orders WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new MedicineOrder(
                        rs.getInt("id"),
                        rs.getInt("medicine_id"),
                        rs.getInt("quantity"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getInt("user_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    
     public void updateOrderStatusToReceived(int orderId) {
        String query = "SELECT medicine_id, quantity FROM medicine_orders WHERE id = ?";
        String updateOrderQuery = "UPDATE medicine_orders SET status = ? WHERE id = ?";
        String updateMedicineQuery = "UPDATE medicines SET quantity_in_stock = quantity_in_stock + ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(query)) {

            // Get the current order's details (medicine_id and quantity)
            selectStmt.setInt(1, orderId);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int medicineId = rs.getInt("medicine_id");
                    int quantityOrdered = rs.getInt("quantity");

                    // Update the status of the order to "Received"
                    try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderQuery)) {
                        updateOrderStmt.setString(1, "Received");
                        updateOrderStmt.setInt(2, orderId);
                        updateOrderStmt.executeUpdate();
                    }

                    // Update the medicine's quantity_in_stock
                    try (PreparedStatement updateMedicineStmt = conn.prepareStatement(updateMedicineQuery)) {
                        updateMedicineStmt.setInt(1, quantityOrdered);
                        updateMedicineStmt.setInt(2, medicineId);
                        updateMedicineStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
