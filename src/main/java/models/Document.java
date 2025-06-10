package com.digitallocker.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Document {
    private String id;
    private String userId;
    private String fileName;
    private String fileType;
    private LocalDate issueDate;
    private String filePath;
    private LocalDateTime uploadedAt;

    public Document() {}

    public Document(String id, String userId, String fileName, String fileType,
                   LocalDate issueDate, String filePath, LocalDateTime uploadedAt) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.issueDate = issueDate;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    public Document(String id, int userId, String fileName, String fileType,
                   LocalDate issueDate, String filePath, LocalDateTime uploadedAt) {
        this(id, String.valueOf(userId), fileName, fileType, issueDate, filePath, uploadedAt);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}