package com.digitallocker.controllers;

import com.digitallocker.models.Document;
import com.digitallocker.services.DocumentService;
import com.digitallocker.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ViewDocumentsController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;
    @FXML private TableView<Document> documentsTable;
    @FXML private TableColumn<Document, String> nameColumn;
    @FXML private TableColumn<Document, String> typeColumn;
    @FXML private TableColumn<Document, String> issueDateColumn;
    @FXML private TableColumn<Document, String> uploadDateColumn;
    @FXML private TableColumn<Document, Void> actionsColumn;
    @FXML private Label statusLabel;

    private DocumentService documentService;
    private ObservableList<Document> allDocuments;
    private ObservableList<Document> filteredDocuments;

    @FXML
    private void initialize() {
        documentService = new DocumentService();
        allDocuments = FXCollections.observableArrayList();
        filteredDocuments = FXCollections.observableArrayList();
        
        setupTable();
        setupFilters();
        loadDocuments();
        statusLabel.setVisible(false);
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        
        issueDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getIssueDate();
            return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        });
        
        uploadDateColumn.setCellValueFactory(cellData -> {
            String dateStr = cellData.getValue().getUploadedAt()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new SimpleStringProperty(dateStr);
        });

        // Actions column with download and delete buttons
        actionsColumn.setCellFactory(param -> new TableCell<Document, Void>() {
            private final Button downloadBtn = new Button("Download");
            private final Button deleteBtn = new Button("Delete");
            private final Button previewBtn = new Button("Preview");

            {
                downloadBtn.setOnAction(event -> {
                    Document doc = getTableView().getItems().get(getIndex());
                    handleDownload(doc);
                });
                
                deleteBtn.setOnAction(event -> {
                    Document doc = getTableView().getItems().get(getIndex());
                    handleDelete(doc);
                });
                
                previewBtn.setOnAction(event -> {
                    Document doc = getTableView().getItems().get(getIndex());
                    handlePreview(doc);
                });

                downloadBtn.getStyleClass().add("table-button");
                deleteBtn.getStyleClass().add("table-button-danger");
                previewBtn.getStyleClass().add("table-button-secondary");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                    buttons.getChildren().addAll(previewBtn, downloadBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        documentsTable.setItems(filteredDocuments);
    }

    private void setupFilters() {
        typeFilterCombo.getItems().addAll(
            "All Types",
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
        typeFilterCombo.setValue("All Types");
    }

    private void loadDocuments() {
        String userId = String.valueOf(SessionManager.getInstance().getCurrentUser().getId());
        List<Document> documents = documentService.getUserDocuments(userId);
        
        allDocuments.clear();
        allDocuments.addAll(documents);
        
        filteredDocuments.clear();
        filteredDocuments.addAll(documents);
        
        showStatus("Loaded " + documents.size() + " documents", true);
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        String selectedType = typeFilterCombo.getValue();
        
        List<Document> filtered = allDocuments.stream()
            .filter(doc -> {
                boolean matchesSearch = searchTerm.isEmpty() || 
                    doc.getFileName().toLowerCase().contains(searchTerm);
                
                boolean matchesType = "All Types".equals(selectedType) || 
                    doc.getFileType().equals(selectedType);
                
                return matchesSearch && matchesType;
            })
            .collect(Collectors.toList());
        
        filteredDocuments.clear();
        filteredDocuments.addAll(filtered);
        
        showStatus("Found " + filtered.size() + " documents", true);
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        typeFilterCombo.setValue("All Types");
        filteredDocuments.clear();
        filteredDocuments.addAll(allDocuments);
        showStatus("Filters cleared", true);
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
            showStatus("Error loading dashboard", false);
        }
    }

    private void handleDownload(Document document) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Document");
        fileChooser.setInitialFileName(document.getFileName());
        
        File saveFile = fileChooser.showSaveDialog(documentsTable.getScene().getWindow());
        
        if (saveFile != null) {
            try {
                Path sourcePath = Paths.get(document.getFilePath());
                Files.copy(sourcePath, saveFile.toPath());
                showStatus("Document downloaded successfully", true);
            } catch (IOException e) {
                showStatus("Error downloading document: " + e.getMessage(), false);
            }
        }
    }

    private void handleDelete(Document document) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Document");
        alert.setHeaderText("Are you sure you want to delete this document?");
        alert.setContentText("Document: " + document.getFileName() + "\nThis action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = documentService.deleteDocument(document.getId());
            if (success) {
                allDocuments.remove(document);
                filteredDocuments.remove(document);
                showStatus("Document deleted successfully", true);
            } else {
                showStatus("Error deleting document", false);
            }
        }
    }

    private void handlePreview(Document document) {
        try {
            Path filePath = Paths.get(document.getFilePath());
            String fileName = document.getFileName().toLowerCase();
            
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                fileName.endsWith(".png") || fileName.endsWith(".gif")) {
                
                // Show image preview
                showImagePreview(document, filePath);
            } else {
                // For other file types, just show file info
                showFileInfo(document);
            }
        } catch (Exception e) {
            showStatus("Error previewing document: " + e.getMessage(), false);
        }
    }

    private void showImagePreview(Document document, Path filePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ImagePreview.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage previewStage = new Stage();
            previewStage.setTitle("Preview - " + document.getFileName());
            previewStage.setScene(scene);
            
            ImagePreviewController controller = loader.getController();
            controller.setDocument(document, filePath);
            
            previewStage.show();
        } catch (IOException e) {
            showFileInfo(document);
        }
    }

    private void showFileInfo(Document document) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Document Information");
        alert.setHeaderText("Document Details");
        
        String info = String.format(
            "Name: %s\nType: %s\nIssue Date: %s\nUploaded: %s\nFile Path: %s",
            document.getFileName(),
            document.getFileType(),
            document.getIssueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            document.getUploadedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            document.getFilePath()
        );
        
        alert.setContentText(info);
        alert.showAndWait();
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
        statusLabel.setVisible(true);
    }
}