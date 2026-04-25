package com.nsbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection – singleton helper that opens/returns a MySQL connection.
 * Adjust DB_URL, DB_USER, and DB_PASS to match your local XAMPP setup.
 */
public class DBConnection {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/nsbankdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";          // default XAMPP password is empty

    private static Connection connection = null;

    /** Returns a live connection; opens one if not yet created or closed. */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DBConnection] Cannot connect to DB: " + e.getMessage());
        }
        return connection;
    }

    /** Closes the connection (call on app shutdown). */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
        }
    }
}
