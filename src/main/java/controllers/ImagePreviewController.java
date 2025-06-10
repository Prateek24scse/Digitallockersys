package com.digitallocker.controllers;

import com.digitallocker.models.Document;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class ImagePreviewController {
    @FXML private ImageView imageView;
    @FXML private Label fileNameLabel;
    @FXML private Label fileTypeLabel;
    @FXML private Label issueDateLabel;
    @FXML private Label uploadDateLabel;

    public void setDocument(Document document, Path filePath) {
        try {
            // Load and display image
            FileInputStream inputStream = new FileInputStream(filePath.toFile());
            Image image = new Image(inputStream);
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(500);
            imageView.setFitHeight(400);
            
            // Set document information
            fileNameLabel.setText("File: " + document.getFileName());
            fileTypeLabel.setText("Type: " + document.getFileType());
            issueDateLabel.setText("Issue Date: " + 
                document.getIssueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            uploadDateLabel.setText("Uploaded: " + 
                document.getUploadedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
        } catch (IOException e) {
            fileNameLabel.setText("Error loading image: " + e.getMessage());
        }
    }
}