package com.example.focustodoapp;

import com.example.focustodoapp.constants.Component;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FocusToDoApplication extends Application {
    double x, y = 0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        String componentName = Component.getComponent(Component.MAIN);
        FXMLLoader fxmlLoader = new FXMLLoader(FocusToDoApplication.class.getResource(componentName));
        Parent root = fxmlLoader.load();
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);
        });

        primaryStage.setTitle("Focus ToDo App");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}