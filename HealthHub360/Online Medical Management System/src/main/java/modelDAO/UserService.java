/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelDAO;

import java.util.List;
import java.sql.SQLException;
import model.Doctor;
import model.Patient;
import model.Person;
import model.Role; 
import model.User;

/**
 *
 * @author keerthichandrakanth
 */
public class UserService {
    
    private static User loggedInUser; // Static field to store the logged-in user

    public static User getLoggedInUser() {
    if (loggedInUser == null) {
        throw new IllegalStateException("No user is currently logged in.");
    }
    return loggedInUser;
}

public static void setLoggedInUser(User user) {
    loggedInUser = user;
}


    /*public static User getLoggedInUser() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }*/
    private UserDAO userDAO;
    private RoleDAO roleDAO;


    public UserService(UserDAO userDAO, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
    }

   // Create a new person
    public Person createPerson(String name, String contactNumber, String email, String address) throws SQLException {
        Person person = new Person(0, name, contactNumber, email, address);
        userDAO.createPerson(person);
        return person;
    }

    // Create a user with a role (Doctor, Patient, Admin, etc.)
    public void createUserWithRole(String username, String password, String roleName, Person person) throws SQLException {
        Role role = roleDAO.getRoleByName(roleName);
        if (role == null) {
            throw new SQLException("Role not found: " + roleName);
        }
        User user = new User(0, username, password, role, person);
        userDAO.createUser(user);
    }

    // Create a doctor and store them in the database
    public void createDoctor(String username, String password, String roleName, Person person, String specialization) throws SQLException {
    // Get the role from the roleDAO
    Role role = roleDAO.getRoleByName(roleName);

    // Check if the role is valid and is "Doctor"
    if (role == null || !role.getRoleName().equals("Doctor")) {
        throw new SQLException("Invalid role for doctor creation");
    }

    // Create user with the "Doctor" role
    User user = new User(0, username, password, role, person);
    userDAO.createUser(user);

    // Now, create doctor and store it in doctors table
    // Doctor's ID is the same as the person's ID, and person.getId() should be used
    Doctor doctor = new Doctor(user.getId(), person.getId(), specialization);
    
    // Create a doctor in the doctors table
    userDAO.createDoctor(doctor);
}

  // Create a patient and store them in the database
public void createPatient(String username, String password, String roleName, Person person, String insuranceNumber, String dateOfBirth) throws SQLException {
    // Get the role from the roleDAO
    Role role = roleDAO.getRoleByName(roleName);
    
    // Check if the role is valid and is "Patient"
    if (role == null || !role.getRoleName().equals("Patient")) {
        throw new SQLException("Invalid role for patient creation");
    }
    
    // Create user with the "Patient" role
    User user = new User(0, username, password, role, person);
    userDAO.createUser(user);

    // Now, create a patient and store it in the patients table
    // Patient's ID is the same as the person's ID, and person.getId() should be used
    Patient patient = new Patient(
        person.getId(), // Use the person's ID
        insuranceNumber,
        dateOfBirth,
        person.getName(), // Assuming `Person` has getName()
        person.getEmail(), // Assuming `Person` has getEmail()
        person.getContactNumber() // Assuming `Person` has getContactNumber()
    );
    
    // Create a patient in the patients table
    userDAO.createPatient(patient);
}
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    /*Passinglogged in user*/    
    public User authenticateUser(String username, String password, String role) throws SQLException {
    User user = userDAO.getUserByUsername(username);
    if (user != null && user.getPassword().equals(password) && user.getRole().getRoleName().equals(role)) {
        loggedInUser = user; 
        return user;
    }
    return null;
}
}
