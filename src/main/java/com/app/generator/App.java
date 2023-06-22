package com.app.generator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);
            stage.setTitle("Project Generator");
        }
        catch (Exception ex)
        {
            System.err.println("No fxml file!");
            System.exit(-1);
        }
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png"))));
        }
        catch (Exception ex)
        {
            System.err.println("No icon!");
        }

    }

    public static void main(String[] args) {
        launch();
    }
}