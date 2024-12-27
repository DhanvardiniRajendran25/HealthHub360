/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Role; 
/**
 *
 * @author keerthichandrakanth
 */
public class RoleDAO {
    private Connection connection;

    public RoleDAO(Connection connection) {
        this.connection = connection;
    }

    public Role getRoleById(int id) throws SQLException {
        String query = "SELECT * FROM roles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Role(rs.getInt("id"), rs.getString("role_name"));
            }
        }
        return null;
    }

    public Role getRoleByName(String roleName) throws SQLException {
        String query = "SELECT * FROM roles WHERE role_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roleName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
               return new Role(rs.getInt("id"), rs.getString("role_name"));
            }
        }
        return null;
    }

    public void createRole(String roleName) throws SQLException {
        String query = "INSERT INTO roles (role_name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roleName);
            stmt.executeUpdate();
        }
    }
    
}
