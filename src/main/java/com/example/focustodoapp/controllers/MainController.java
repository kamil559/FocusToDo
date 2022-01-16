package com.example.focustodoapp.controllers;

import com.example.focustodoapp.models.ModelInterface;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private boolean opacityPaneState;
    private boolean drawerPaneState;

    ModelInterface modelInterface = new ModelInterface();

    @FXML
    private ImageView drawerImage, loginWindowClose;   // customowy guzik

    @FXML
    private AnchorPane mainPane, opacityPane, drawerPane, loginOpacityPane, signInPane, signUpPane;

    @FXML
    private StackPane loginWindow;

    @FXML
    private Label isConnected;

    @FXML
    private Button loginPageButton, loginSubmitButton, openSignUpPane, backToSignInPane;

    @FXML
    private TextField usernameLoginInput, passwordLoginInput, usernameSignUpInput, passwordSignUpInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        ToDo: only for debugging purposes!
//         Remove when the project is done
        if (modelInterface.isDbConnected()) {
            isConnected.setStyle("-fx-background-color: green");
        } else {
            isConnected.setStyle("-fx-background-color: red");
        }

        opacityPaneState = false;
        drawerPaneState = false;
        loginOpacityPane.setVisible(false);
        loginWindow.setVisible(false);
        initOpacityPane();

        TranslateTransition translateTransition =  new TranslateTransition(Duration.seconds(0.3), drawerPane);
        translateTransition.setByX(-600);
        translateTransition.play();

        drawerImage.setOnMouseClicked(event -> {
            drawerImageClickHandler();
        });

        opacityPane.setOnMouseClicked(event -> {
            opacityPaneClickHandler();
        });

        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    if (loginWindow.isVisible()) {
                        hideLoginWindow();
                    } else if (drawerPaneState) {
                        hideDrawer();
                        FadeTransition fadeTransition = hideOpacity(opacityPane);
                        fadeTransition.setOnFinished(event -> {
                            opacityPane.setVisible(false);
                            opacityPaneState = false;
                        });
                    }
                }
            }
        });

        setSignInEvents();
        setSignUpEvents();

//        ToDo:
//         1) Wyświetla się okno logowania z wyborem:
//            - zarejestruj
//            - kontynuuj bez logowania (dane będą trzymane w pamięci i po zamknięciu aplikacji zostaną utracone)
//            - zarejestrowaniu możemy od razu się zalogować
//         2) Po zalogowaniu pojawia się ekran główny, w którym mamy przyciski:
//            - Dodaj projekt
//            - Dodaj zadanie (do wyboru projekt)
//         3) Możemy nawigować po różnych zakładkach (otwarte menu powinno przesuwać cały widok w prawą stronę)
    }

    private void setSignUpEvents() {
        signUpPane.setVisible(false);

        openSignUpPane.setOnMouseClicked(event -> {
            showSignUpPane();
        });

        backToSignInPane.setOnMouseClicked(event -> {
            hideSignUpPane();
        });
    }

    private void showSignUpPane() {
        signInPane.setVisible(false);
        signUpPane.setVisible(true);
    }

    private void hideSignUpPane() {
        signUpPane.setVisible(false);
        signInPane.setVisible(true);
        usernameSignUpInput.clear();
        passwordSignUpInput.clear();
    }

    private void setSignInEvents() {
        loginOpacityPane.setOnMouseClicked(event -> {
            loginOpacityPaneClickHandler();
        });

        loginPageButton.setOnMouseClicked(event -> {
            showLoginWindow();
        });

        loginWindowClose.setOnMouseClicked(event -> {
            hideLoginWindow();
        });

        loginSubmitButton.setOnMouseClicked(event -> {
            loginHandler();
        });

        usernameLoginInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    loginHandler();
                }
            }
        });

        passwordLoginInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    loginHandler();
                }
            }
        });
    }

    private void loginHandler() {
        String username = usernameLoginInput.getText();
        String password = passwordLoginInput.getText();

        if (Objects.equals(username, "abc") & Objects.equals(password, "zaq1@WSX")) {
            usernameLoginInput.setText("Zalogowano poprawnie!");
        }
    }

    private void showLoginWindow() {
        loginWindow.setVisible(true);
        showOpacity(loginOpacityPane);
    }

    private void hideLoginWindow() {
        loginWindow.setVisible(false);
        usernameLoginInput.clear();
        passwordLoginInput.clear();
        hideLoginOpacityPane();
    }

    private void initOpacityPane() {
        opacityPane.setVisible(false);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), opacityPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    private void drawerImageClickHandler() {
        drawerPaneState = !drawerPaneState;
        if (drawerPaneState) {
            showDrawer();
            FadeTransition fadeTransition = showOpacity(opacityPane);
            fadeTransition.setOnFinished(event -> {
                opacityPaneState = true;
            });
        } else {
            hideDrawer();
            FadeTransition fadeTransition = hideOpacity(opacityPane);
            fadeTransition.setOnFinished(event -> {
                opacityPane.setVisible(false);
                opacityPaneState = false;
            });
        }
    }

    private void opacityPaneClickHandler() {
        opacityPaneState = !opacityPaneState;
        if (opacityPaneState) {
            FadeTransition fadeTransition = showOpacity(opacityPane);
            fadeTransition.setOnFinished(event -> {
                opacityPaneState = true;
            });
            showDrawer();
        } else {
            FadeTransition fadeTransition = hideOpacity(opacityPane);
            fadeTransition.setOnFinished(event -> {
                opacityPane.setVisible(false);
                opacityPaneState = false;
            });
            hideDrawer();
        }
    }

    private void loginOpacityPaneClickHandler() {
        hideLoginWindow();
    }

    private void hideLoginOpacityPane() {
        FadeTransition fadeTransition = hideOpacity(loginOpacityPane, 0.01);
        fadeTransition.setOnFinished(event -> {
            loginOpacityPane.setVisible(false);
        });
    }

    public void showDrawer() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), drawerPane);
        translateTransition.setByX(+600);
        translateTransition.play();

        translateTransition.setOnFinished(event -> {
            drawerPaneState = true;
        });
    }

    public void hideDrawer() {
        TranslateTransition translateTransition =  new TranslateTransition(Duration.seconds(0.3), drawerPane);
        translateTransition.setByX(-600);
        translateTransition.play();

        translateTransition.setOnFinished(event -> {
            drawerPaneState = false;
        });
    }

    public FadeTransition showOpacity(AnchorPane pane) {
        pane.setVisible(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), pane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(0.15);
        fadeTransition.play();
        return fadeTransition;
    }

    public FadeTransition hideOpacity(AnchorPane pane) {
        double defaultDuration = 0.3;
        return doHideOpacity(pane, defaultDuration);
    }

    public FadeTransition hideOpacity(AnchorPane pane, double duration) {
        return doHideOpacity(pane, duration);
    }

    private FadeTransition doHideOpacity(AnchorPane pane, double duration) {
        pane.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration), pane);
        fadeTransition.setFromValue(0.15);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        return fadeTransition;
    }
}