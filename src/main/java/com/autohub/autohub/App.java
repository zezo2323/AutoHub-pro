package com.autohub.autohub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource("/fxml/auth.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);

        scene.getStylesheets().add(
                App.class.getResource("/styles/app.css").toExternalForm()
        );

        stage.setTitle("DriveNow - Admin Panel");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(700);

        // ✅ إضافة الأيقونة
        try {
            Image icon = new Image(
                    App.class.getResourceAsStream("/images/app-icon.png")
            );
            stage.getIcons().add(icon);
            System.out.println("✅ App icon loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to load app icon: " + e.getMessage());
        }

        stage.show();
    }
}
