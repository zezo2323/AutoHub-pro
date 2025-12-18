package com.autohub.autohub.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnector {
    private static final String URL = "jdbc:mysql://localhost:3306/car_rental";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // تحميل الـ Driver يدوي 👇 (السطر الجديد)
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection failed. (Make sure XAMPP MySQL is running)");
            System.err.println("SQL Error: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
            return null;
        }
    }

    static void main(String[] args) {
        System.out.println("... Testing Java connection to the database ...");
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Database connection succeeded!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
