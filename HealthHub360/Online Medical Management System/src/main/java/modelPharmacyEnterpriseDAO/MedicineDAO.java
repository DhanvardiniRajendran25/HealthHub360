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
import modelPharmacyEnterprise.Medicine;
import ui.DatabaseUtil;

/**
 *
 * @author keerthichandrakanth
 */
public class MedicineDAO {
    
      public void create(Medicine medicine) {
        String query = "INSERT INTO medicines (name, description, price, quantity_in_stock, reorder_level, supplier, expiry_date) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getDescription());
            stmt.setBigDecimal(3, medicine.getPrice());
            stmt.setInt(4, medicine.getQuantityInStock());
            stmt.setInt(5, medicine.getReorderLevel());
            stmt.setString(6, medicine.getSupplier());
            stmt.setDate(7, medicine.getExpiryDate());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Medicine read(int id) {
        String query = "SELECT * FROM medicines WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Medicine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("reorder_level"),
                        rs.getString("supplier"),
                        rs.getDate("expiry_date")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Medicine medicine) {
        String query = "UPDATE medicines SET name = ?, description = ?, price = ?, quantity_in_stock = ?, " +
                       "reorder_level = ?, supplier = ?, expiry_date = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getDescription());
            stmt.setBigDecimal(3, medicine.getPrice());
            stmt.setInt(4, medicine.getQuantityInStock());
            stmt.setInt(5, medicine.getReorderLevel());
            stmt.setString(6, medicine.getSupplier());
            stmt.setDate(7, medicine.getExpiryDate());
            stmt.setInt(8, medicine.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM medicines WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Medicine> getAll() {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM medicines";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                medicines.add(new Medicine(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity_in_stock"),
                    rs.getInt("reorder_level"),
                    rs.getString("supplier"),
                    rs.getDate("expiry_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }
    
    
 // Update the stock for a specific medicine
     public boolean updateMedicineStock(int medicineId, int newStock) {
        String query = "UPDATE medicines SET quantity_in_stock = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, medicineId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating medicine stock.");
            return false;
        }
    }
    
      public List<Object[]> fetchMedicineStockUpdateData(int prescriptionId) {
        String query = """
            SELECT m.id AS medicine_id, pm.quantity AS prescribed_quantity, 
                   m.quantity_in_stock AS current_stock
            FROM prescription_medicines pm
            JOIN medicines m ON pm.medicine_id = m.id
            JOIN prescriptions p ON p.id = pm.prescription_id
            WHERE p.id = ?;
        """;

        List<Object[]> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, prescriptionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int medicineId = rs.getInt("medicine_id");
                int prescribedQuantity = rs.getInt("prescribed_quantity");
                int currentStock = rs.getInt("current_stock");
                results.add(new Object[]{medicineId, prescribedQuantity, currentStock});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching stock update data.");
        }

        return results;
    }
   
}
