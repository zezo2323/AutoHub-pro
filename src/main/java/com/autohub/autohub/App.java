package com.autohub.autohub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    static void main(String[] args) {   // خليك حاطط public
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource("/fxml/dashboard.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);

        // ربط ملف الـ CSS (اتأكد من المسار)
        scene.getStylesheets().add(
                App.class.getResource("/styles/app.css").toExternalForm()
        );

        stage.setTitle("DriveNow - Admin Panel");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        stage.show();
    }
}
