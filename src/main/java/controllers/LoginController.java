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
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button signupButton;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        try {
            User user = authenticateUser(username, password);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                loadDashboard();
            } else {
                showError("Invalid username or password");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Error loading signup page");
        }
    }

    private User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("salt");
                
                if (PasswordUtil.verifyPassword(password, salt, storedHash)) {
                    return new User(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        storedHash,
                        salt,
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getInt("failed_login_attempts")
                    );
                }
            }
        }
        return null;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Digital Locker - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Error loading dashboard");
        }
    }
}