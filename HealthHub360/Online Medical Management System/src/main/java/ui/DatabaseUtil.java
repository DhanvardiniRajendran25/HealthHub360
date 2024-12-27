/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author keerthichandrakanth
 */
public class DatabaseUtil {
    // Database credentials

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=OMMSFINAL;encrypt=false;";
    private static final String USER = "sa";
    private static final String PASSWORD = "Anjana@3023";

    private static final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

    // Method to get a connection
    public static Connection getConnection() throws SQLException {
        Connection connection = threadConnection.get();
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established.");
        } else {
            System.out.println("Reusing existing database connection.");
        }
        return connection;
    }

    public static ResultSet executeQuery(String query) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    // Method to close the connection
    public static void closeConnection() throws SQLException {
        Connection connection = threadConnection.get();
        if (connection != null && !connection.isClosed()) {
            connection.close();
            threadConnection.remove(); 
        }
    }

}
