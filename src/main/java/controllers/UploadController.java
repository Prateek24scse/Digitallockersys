package com.digitallocker.controllers;

import com.digitallocker.models.Document;
import com.digitallocker.services.DocumentService;
import com.digitallocker.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UploadController {
    @FXML private TextField fileNameField;
    @FXML private ComboBox<String> fileTypeCombo;
    @FXML private DatePicker issueDatePicker;
    @FXML private Label selectedFileLabel;
    @FXML private Button chooseFileButton;
    @FXML private Button uploadButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private File selectedFile;
    private DocumentService documentService;

    @FXML
    private void initialize() {
        documentService = new DocumentService();
        setupFileTypes();
        statusLabel.setVisible(false);
    }

    private void setupFileTypes() {
        fileTypeCombo.getItems().addAll(
            "Identity Document",
            "Academic Certificate",
            "Professional License",
            "Medical Record",
            "Financial Document",
            "Legal Document",
            "Insurance Paper",
            "Property Document",
            "Other"
        );
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Document");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.txt")
        );

        selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        
        if (selectedFile != null) {
            selectedFileLabel.setText("Selected: " + selectedFile.getName());
            if (fileNameField.getText().trim().isEmpty()) {
                fileNameField.setText(selectedFile.getName());
            }
        } else {
            selectedFileLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleUpload() {
        String fileName = fileNameField.getText().trim();
        String fileType = fileTypeCombo.getValue();
        LocalDate issueDate = issueDatePicker.getValue();

        if (fileName.isEmpty() || fileType == null || issueDate == null || selectedFile == null) {
            showStatus("Please fill in all fields and select a file", false);
            return;
        }

        try {
            Document document = new Document(
                UUID.randomUUID().toString(),
                SessionManager.getInstance().getCurrentUser().getId(),
                fileName,
                fileType,
                issueDate,
                "", // Will be set by DocumentService
                LocalDateTime.now()
            );

            boolean success = documentService.uploadDocument(document, selectedFile);
            
            if (success) {
                showStatus("Document uploaded successfully!", true);
                clearForm();
            } else {
                showStatus("Failed to upload document", false);
            }
        } catch (Exception e) {
            showStatus("Error: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Digital Locker - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        fileNameField.clear();
        fileTypeCombo.setValue(null);
        issueDatePicker.setValue(null);
        selectedFile = null;
        selectedFileLabel.setText("No file selected");
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
        statusLabel.setVisible(true);
    }
}