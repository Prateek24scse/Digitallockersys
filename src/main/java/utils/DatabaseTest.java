package com.digitallocker.utils;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            System.out.println("Attempting to connect to database...");
            dbManager.initializeDatabase();
            System.out.println("Database connection successful!");
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 