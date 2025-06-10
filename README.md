# Digital Locker System

A JavaFX-based desktop application for secure document management with MySQL database integration.

## Features

- **User Authentication**: Secure signup/login with password hashing
- **Document Management**: Upload, view, download, and delete documents
- **File Organization**: Categorize documents by type with issue dates
- **Search & Filter**: Find documents by name, type, or date
- **Image Preview**: Preview image documents directly in the application
- **Secure Storage**: Files stored locally with database metadata
- **User Profile**: Manage account settings and change passwords
- **Modern UI**: Clean, professional interface with your brand colors

## Technologies

- Java 24
- JavaFX for UI
- Maven for build management
- MySQL with JDBC for database
- SHA-256 for password hashing

## Setup Instructions

### 1. Prerequisites
- Java 24 or later
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### 2. Database Setup
1. Install and start MySQL server
2. Run the SQL script in `supabase/migrations/20250610092340_wooden_castle.sql` to create the database and tables
3. Update database credentials in `DatabaseManager.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/digitallocker";
   private static final String USER = "your_mysql_username";
   private static final String PASSWORD = "your_mysql_password";
   ```

### 3. Build and Run
```bash
# Clone/download the project
cd digital-locker

# Install dependencies
mvn clean install

# Run the application
mvn javafx:run
```

### 4. Alternative Run Methods
```bash
# Compile and run manually
mvn compile
mvn exec:java -Dexec.mainClass="com.digitallocker.Main"

# Create executable JAR
mvn clean package
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/digital-locker-1.0.0.jar
```

## Project Structure

```
digital-locker/
├── src/main/java/com/digitallocker/
│   ├── Main.java                    # Application entry point
│   ├── controllers/                 # FXML controllers
│   │   ├── LoginController.java
│   │   ├── SignupController.java
│   │   ├── DashboardController.java
│   │   ├── UploadController.java
│   │   ├── ViewDocumentsController.java
│   │   ├── ProfileController.java
│   │   └── ImagePreviewController.java
│   ├── models/                      # Data models
│   │   ├── User.java
│   │   └── Document.java
│   ├── services/                    # Business logic
│   │   └── DocumentService.java
│   └── utils/                       # Utilities
│       ├── DatabaseManager.java
│       ├── PasswordUtil.java
│       └── SessionManager.java
├── src/main/resources/
│   ├── fxml/                        # FXML layout files
│   │   ├── Login.fxml
│   │   ├── Signup.fxml
│   │   ├── Dashboard.fxml
│   │   ├── Upload.fxml
│   │   ├── ViewDocuments.fxml
│   │   ├── Profile.fxml
│   │   └── ImagePreview.fxml
│   └── styles/
│       └── style.css                # UI styling
├── storage/                         # Document storage directory
├── supabase/migrations/             # Database initialization
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## Usage

1. **First Time Setup**: Run the application and create a new account
2. **Login**: Use your credentials to access the dashboard
3. **Upload Documents**: Add new documents with metadata
4. **View Documents**: Browse, search, and filter your documents
5. **Preview**: View image documents directly in the app
6. **Download**: Save documents to your local system
7. **Delete**: Remove documents with confirmation
8. **Profile**: Update your account settings and change password

## Security Features

- Password hashing with SHA-256
- Security questions for account recovery
- Session management
- File access control per user
- SQL injection prevention with prepared statements

## Document Management Features

- **Upload**: Support for all file types with metadata
- **View**: Table view with sorting and filtering
- **Search**: Find documents by name or type
- **Filter**: Filter by document type
- **Preview**: Image preview for supported formats
- **Download**: Save documents to any location
- **Delete**: Secure deletion with confirmation

## Customization

- **Colors**: Modify the CSS variables in `style.css`
- **Database**: Update connection settings in `DatabaseManager.java`
- **File Types**: Add more document categories in the upload form
- **Storage Path**: Change the storage location in `DocumentService.java`

## Troubleshooting

### Common Issues

1. **JavaFX Module Error**: Ensure JavaFX is properly configured in your IDE
2. **Database Connection**: Verify MySQL is running and credentials are correct
3. **File Permissions**: Check that the application can create directories in the storage folder
4. **Missing Dependencies**: Run `mvn clean install` to download all dependencies

### MySQL Connection Issues
```bash
# Test MySQL connection
mysql -u your_username -p -h localhost -P 3306

# Check if database exists
SHOW DATABASES;
USE digitallocker;
SHOW TABLES;
```

## Future Enhancements

- [ ] Document preview for more file types (PDF, Word, etc.)
- [ ] Advanced search with full-text indexing
- [ ] Document sharing between users
- [ ] Backup and restore functionality
- [ ] Document versioning
- [ ] Encrypted file storage
- [ ] Audit logging
- [ ] Email notifications
- [ ] Document categories and tags
- [ ] Bulk operations

## License

This project is created for educational purposes. Feel free to modify and distribute as needed.