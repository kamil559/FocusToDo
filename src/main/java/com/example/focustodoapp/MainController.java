package com.example.focustodoapp;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private boolean opacityState;
    private boolean drawerState;

    @FXML
    private ImageView drawerImage;   // customowy guzik

    @FXML
    private AnchorPane opacityPane, drawerPane;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        opacityState = false;
        drawerState = false;
        initShadePanes();

        TranslateTransition translateTransition =  new TranslateTransition(Duration.seconds(0.3), drawerPane);
        translateTransition.setByX(-600);
        translateTransition.play();

        drawerImage.setOnMouseClicked(event -> {
            drawerImageClickHandler();
        });

        opacityPane.setOnMouseClicked(event -> {
            opacityPaneClickHandler();
        });
    }

    private void initShadePanes() {
        opacityPane.setVisible(false);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), opacityPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    private void drawerImageClickHandler() {
        drawerState = !drawerState;
        if (drawerState) {
            showDrawer();
            showOpacity();
        } else {
            hideDrawer();
            hideOpacity();
        }
    }

    private void opacityPaneClickHandler() {
        opacityState = !opacityState;
        if (opacityState) {
            showOpacity();
            showDrawer();
        } else {
            hideOpacity();
            hideDrawer();
        }
    }

    public void showDrawer() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), drawerPane);
        translateTransition.setByX(+600);
        translateTransition.play();

        translateTransition.setOnFinished(event -> {
            drawerState = true;
        });
    }

    public void hideDrawer() {
        TranslateTransition translateTransition =  new TranslateTransition(Duration.seconds(0.3), drawerPane);
        translateTransition.setByX(-600);
        translateTransition.play();

        translateTransition.setOnFinished(event -> {
            drawerState = false;
        });
    }

    public void showOpacity() {
        opacityPane.setVisible(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), opacityPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(0.15);
        fadeTransition.play();

        fadeTransition.setOnFinished(event -> {
            opacityState = true;
        });
    }

    public void hideOpacity() {
        opacityPane.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), opacityPane);
        fadeTransition.setFromValue(0.15);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        fadeTransition.setOnFinished(event1 -> {
            opacityPane.setVisible(false);
            opacityState = false;
        });
    }
}