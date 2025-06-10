package com.digitallocker.utils;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/digital_locker?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "digilocker";
    private static final String PASSWORD = "digilocker123";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static String getDriver() {
        return DRIVER;
    }
} 