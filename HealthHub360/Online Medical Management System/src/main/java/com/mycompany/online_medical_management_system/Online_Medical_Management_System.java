/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.online_medical_management_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import model.Role; 
import model.User;
import modelDAO.RoleDAO;
import modelDAO.UserDAO;
import ui.DatabaseUtil;
import ui.mainJFrame;

/**
 *
 * @author keerthichandrakanth
 */
public class Online_Medical_Management_System {
    
        public static void main(String[] args) {
        // Step 1: Establish database connection
        try {
            // Establish a connection using DatabaseUtil
            Connection connection = DatabaseUtil.getConnection();
            
            if (connection != null) {
                System.out.println("Successfully connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error:");
            e.printStackTrace();
        }

        // Step 2: Launch the main GUI window after the connection is successful
        java.awt.EventQueue.invokeLater(() -> {
            new mainJFrame().setVisible(true);  // Open the main JFrame of the UI
        });
    }

 }
