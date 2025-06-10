package com.digitallocker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.digitallocker.utils.DatabaseManager;

import java.io.IOException;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize database
        DatabaseManager.getInstance().initializeDatabase();
        
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        
        primaryStage.setTitle("Digital Locker System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}