package com.digitallocker.controllers;

import com.digitallocker.models.User;
import com.digitallocker.utils.DatabaseManager;
import com.digitallocker.utils.PasswordUtil;
import com.digitallocker.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ProfileController {
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label createdAtLabel;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changePasswordButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        loadUserProfile();
        statusLabel.setVisible(false);
    }

    private void loadUserProfile() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText("Username: " + currentUser.getUsername());
            emailLabel.setText("Email: " + currentUser.getEmail());
            createdAtLabel.setText("Member Since: " + 
                currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showStatus("Please fill in all password fields", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showStatus("New passwords do not match", false);
            return;
        }

        if (newPassword.length() < 8) {
            showStatus("New password must be at least 8 characters long", false);
            return;
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getSalt(), currentUser.getPasswordHash())) {
            showStatus("Current password is incorrect", false);
            return;
        }

        try {
            if (updatePassword(currentUser.getId(), newPassword)) {
                showStatus("Password changed successfully", true);
                clearPasswordFields();
            } else {
                showStatus("Error changing password", false);
            }
        } catch (SQLException e) {
            showStatus("Database error: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Digital Locker - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            showStatus("Error loading dashboard", false);
        }
    }

    private boolean updatePassword(String userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String salt = PasswordUtil.generateSalt();
            String passwordHash = PasswordUtil.hashPassword(newPassword, salt);
            
            stmt.setString(1, passwordHash);
            stmt.setString(2, salt);
            stmt.setString(3, userId);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                // Update the user in session with new password hash and salt
                User currentUser = SessionManager.getInstance().getCurrentUser();
                currentUser = new User(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    currentUser.getEmail(),
                    passwordHash,
                    salt,
                    currentUser.getCreatedAt(),
                    currentUser.getStatus(),
                    currentUser.getFailedLoginAttempts()
                );
                SessionManager.getInstance().setCurrentUser(currentUser);
                return true;
            }
            return false;
        }
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
        statusLabel.setVisible(true);
    }
}