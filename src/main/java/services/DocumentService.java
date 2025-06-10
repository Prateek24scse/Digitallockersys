package com.digitallocker.services;

import com.digitallocker.models.Document;
import com.digitallocker.utils.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentService {
    private static final String STORAGE_BASE_PATH = "storage";

    public boolean uploadDocument(Document document, File sourceFile) {
        try {
            // Create user storage directory
            String userStoragePath = STORAGE_BASE_PATH + File.separator + document.getUserId();
            Path userDir = Paths.get(userStoragePath);
            Files.createDirectories(userDir);

            // Copy file to storage
            String fileName = document.getId() + "_" + sourceFile.getName();
            Path targetPath = userDir.resolve(fileName);
            Files.copy(sourceFile.toPath(), targetPath);

            // Update document with file path
            document.setFilePath(targetPath.toString());

            // Save to database
            return saveDocumentToDatabase(document);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Document> getUserDocuments(String userId) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM documents WHERE user_id = ? ORDER BY uploaded_at DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Document doc = new Document(
                    rs.getString("id"),
                    rs.getString("user_id"),
                    rs.getString("file_name"),
                    rs.getString("file_type"),
                    rs.getDate("issue_date").toLocalDate(),
                    rs.getString("file_path"),
                    rs.getTimestamp("uploaded_at").toLocalDateTime()
                );
                documents.add(doc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documents;
    }

    public boolean deleteDocument(String documentId) {
        try {
            // Get document from database
            Document document = getDocumentById(documentId);
            if (document == null) return false;

            // Delete file from storage
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);

            // Delete from database
            String sql = "DELETE FROM documents WHERE id = ?";
            try (Connection conn = DatabaseManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, documentId);
                return stmt.executeUpdate() > 0;
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveDocumentToDatabase(Document document) throws SQLException {
        String sql = "INSERT INTO documents (id, user_id, file_name, file_type, issue_date, file_path) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, document.getId());
            stmt.setString(2, document.getUserId());
            stmt.setString(3, document.getFileName());
            stmt.setString(4, document.getFileType());
            stmt.setDate(5, java.sql.Date.valueOf(document.getIssueDate()));
            stmt.setString(6, document.getFilePath());

            return stmt.executeUpdate() > 0;
        }
    }

    private Document getDocumentById(String documentId) throws SQLException {
        String sql = "SELECT * FROM documents WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Document(
                    rs.getString("id"),
                    rs.getString("user_id"),
                    rs.getString("file_name"),
                    rs.getString("file_type"),
                    rs.getDate("issue_date").toLocalDate(),
                    rs.getString("file_path"),
                    rs.getTimestamp("uploaded_at").toLocalDateTime()
                );
            }
        }

        return null;
    }
}