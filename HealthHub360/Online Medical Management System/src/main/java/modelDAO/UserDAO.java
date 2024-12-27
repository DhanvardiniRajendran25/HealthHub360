/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelDAO;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.Doctor;
import model.Patient;
import model.Person;
import model.Role;
import model.User;

/**
 *
 * @author keerthichandrakanth
 */
public class UserDAO {

    private final Connection connection;
    private final RoleDAO roleDAO;

    public UserDAO(Connection connection) {
        this.connection = connection;
        this.roleDAO = new RoleDAO(connection);
    }

    /**
     * Inserts a new user into the database.
     */
    // Create a person in the database
    public void createPerson(Person person) throws SQLException {
        String query = "INSERT INTO persons (name, contact_number, email, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, person.getName());
            stmt.setString(2, person.getContactNumber());
            stmt.setString(3, person.getEmail());
            stmt.setString(4, person.getAddress());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    person.setId(rs.getInt(1));  // Set the generated ID of the person
                }
            }
        }
    }

    // Create a user in the database
    public void createUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, role_id, person_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getRole().getId());
            stmt.setInt(4, user.getPerson().getId());  // Reference the person ID
            stmt.executeUpdate();
        }
    }

    // Insert a doctor into the doctors table
    public void createDoctor(Doctor doctor) throws SQLException {
        String query = "INSERT INTO doctors (person_id, specialization) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, doctor.getId());  // Get the person ID from the inherited method in Person
            stmt.setString(2, doctor.getSpecialization());  // Set the specialization
            stmt.executeUpdate();
        }
    }

    // Insert a patient into the patients table
    public void createPatient(Patient patient) throws SQLException {
        String query = "INSERT INTO patients (person_id, insurance_number, date_of_birth) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Use patient.getId() to get the inherited person_id
            stmt.setInt(1, patient.getId());  // The ID is inherited from Person
            stmt.setString(2, patient.getInsuranceNumber());  // Get insurance number from the Patient class
            stmt.setDate(3, java.sql.Date.valueOf(patient.getDateOfBirth()));  // Convert the date to SQL format
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a user by their username.
     */
    public User getUserByUsername(String username) throws SQLException {
        String query = """
        SELECT u.id AS user_id, u.username, u.password, u.role_id, p.id AS person_id, p.name, p.contact_number, p.email, p.address
        FROM users u
        JOIN persons p ON u.person_id = p.id
        WHERE u.username = ?
    """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = roleDAO.getRoleById(rs.getInt("role_id"));
                    Person person = new Person(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("contact_number"),
                            rs.getString("email"),
                            rs.getString("address")
                    );
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            role,
                            person
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all users from the database.
     */
    /**
     * Retrieves all users from the database.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = """
        SELECT u.id AS user_id, u.username, u.password, u.role_id, p.id AS person_id, p.name, p.contact_number, p.email, p.address
        FROM users u
        JOIN persons p ON u.person_id = p.id
    """;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Role role = roleDAO.getRoleById(rs.getInt("role_id")); // Retrieve role details
                Person person = new Person( // Retrieve person details
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("address")
                );
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role,
                        person
                ));
            }
        }
        return users;
    }

    //Enterprise Admin DAO Methods
    public List<Map<String, Object>> getUsersForEnterprise(int roleId) throws SQLException {
        String query = "SELECT u.id, p.name, p.contact_number, p.email, p.address, r.role_name "
                + "FROM users u "
                + "JOIN persons p ON u.person_id = p.id "
                + "JOIN roles r ON u.role_id = r.id "
                + "WHERE r.id != ?"; // Exclude System Admin

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> users = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("ID", rs.getInt("id"));
                user.put("Name", rs.getString("name"));
                user.put("Contact", rs.getString("contact_number"));
                user.put("Email", rs.getString("email"));
                user.put("Address", rs.getString("address"));
                user.put("Role", rs.getString("role_name"));
                users.add(user);
            }
            return users;
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean addUser(String username, String password, int roleId, String name, String contact, String email, String address) throws SQLException {
        String personQuery = "INSERT INTO persons (name, contact_number, email, address) VALUES (?, ?, ?, ?)";
        String userQuery = "INSERT INTO users (username, password, role_id, person_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement personStmt = connection.prepareStatement(personQuery, Statement.RETURN_GENERATED_KEYS); PreparedStatement userStmt = connection.prepareStatement(userQuery)) {

            // Insert person
            personStmt.setString(1, name);
            personStmt.setString(2, contact);
            personStmt.setString(3, email);
            personStmt.setString(4, address);
            personStmt.executeUpdate();

            try (ResultSet personRs = personStmt.getGeneratedKeys()) {
                if (personRs.next()) {
                    int personId = personRs.getInt(1);

                    userStmt.setString(1, username);
                    userStmt.setString(2, password);
                    userStmt.setInt(3, roleId);
                    userStmt.setInt(4, personId);
                    return userStmt.executeUpdate() > 0;
                }
            }
        }
        return false;
    }

    public boolean updateUserWithRole(int userId, String name, String contact, String email, String address, int roleId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String updatePersonQuery
                    = "UPDATE persons SET name = ?, contact_number = ?, email = ?, address = ? "
                    + "WHERE id = (SELECT person_id FROM users WHERE id = ?)";
            try (PreparedStatement stmt = connection.prepareStatement(updatePersonQuery)) {
                stmt.setString(1, name);
                stmt.setString(2, contact);
                stmt.setString(3, email);
                stmt.setString(4, address);
                stmt.setInt(5, userId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated <= 0) {
                    connection.rollback();
                    return false;
                }
            }

            String updateUserQuery = "UPDATE users SET role_id = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateUserQuery)) {
                stmt.setInt(1, roleId);
                stmt.setInt(2, userId);
                int userUpdated = stmt.executeUpdate();
                if (userUpdated <= 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public boolean deactivateUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    //System Admin DAO
    public List<Map<String, Object>> getEnterpriseAdmins() throws SQLException {
        String query = """
        SELECT u.id, p.name, p.contact_number, p.email, p.address, r.role_name
        FROM users u
        JOIN persons p ON u.person_id = p.id
        JOIN roles r ON u.role_id = r.id
        WHERE r.role_name = 'Enterprise Admin'
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> admins = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> admin = new HashMap<>();
                admin.put("ID", rs.getInt("id"));
                admin.put("Name", rs.getString("name"));
                admin.put("Contact", rs.getString("contact_number"));
                admin.put("Email", rs.getString("email"));
                admin.put("Address", rs.getString("address"));
                admin.put("Role", rs.getString("role_name"));
                admins.add(admin);
            }
            return admins;
        }
    }
}
