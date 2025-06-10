package com.digitallocker.controllers;

import com.digitallocker.utils.DatabaseManager;
import com.digitallocker.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SignupController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Button signupButton;
    @FXML private Button backToLoginButton;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (password.length() < 8) {
            showError("Password must be at least 8 characters long");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
            return;
        }

        try {
            if (createUser(username, email, password)) {
                showSuccess("Account created successfully!");
                handleBackToLogin();
            } else {
                showError("Username or email already exists");
            }
        } catch (SQLException e) {
            showError("Error creating account: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Error returning to login page");
        }
    }

    private boolean createUser(String username, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (id, username, email, password_hash, salt, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String salt = PasswordUtil.generateSalt();
            String passwordHash = PasswordUtil.hashPassword(password, salt);
            
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, passwordHash);
            stmt.setString(5, salt);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                return false;
            }
            throw e;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #27ae60;");
        errorLabel.setVisible(true);
    }
}