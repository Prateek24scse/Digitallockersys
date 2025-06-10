package com.digitallocker.controllers;

import com.digitallocker.services.DocumentService;
import com.digitallocker.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML private BorderPane mainPane;
    @FXML private Label welcomeLabel;
    @FXML private Label documentCountLabel;
    @FXML private Button uploadButton;
    @FXML private Button viewDocumentsButton;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;

    private DocumentService documentService;

    @FXML
    private void initialize() {
        documentService = new DocumentService();
        String username = SessionManager.getInstance().getCurrentUser().getUsername();
        welcomeLabel.setText("Welcome, " + username + "!");
        
        loadDocumentCount();
    }

    private void loadDocumentCount() {
        String userId = String.valueOf(SessionManager.getInstance().getCurrentUser().getId());
        int count = documentService.getUserDocuments(userId).size();
        documentCountLabel.setText("Total Documents: " + count);
    }

    @FXML
    private void handleUpload() {
        loadView("/fxml/Upload.fxml", "Upload Document");
    }

    @FXML
    private void handleViewDocuments() {
        loadView("/fxml/ViewDocuments.fxml", "My Documents");
    }

    @FXML
    private void handleProfile() {
        loadView("/fxml/Profile.fxml", "User Profile");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 500);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Digital Locker - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Digital Locker - " + title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}