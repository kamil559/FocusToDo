package com.example.focustodoapp.controllers;

import com.example.focustodoapp.TaskElement;
import com.example.focustodoapp.dtos.Project;
import com.example.focustodoapp.dtos.Task;
import com.example.focustodoapp.dtos.User;
import com.example.focustodoapp.errors.AuthenticationError;
import com.example.focustodoapp.errors.DatabaseException;
import com.example.focustodoapp.errors.ValidationError;
import com.example.focustodoapp.models.ProjectModel;
import com.example.focustodoapp.models.TaskModel;
import com.example.focustodoapp.models.UserModel;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    private boolean opacityPaneState;
    private boolean drawerPaneState;
    private User user;

    @FXML
    private ImageView drawerImage, loginWindowClose, loginWindowClose2, closeSuccessAlertButton, closeErrorAlertButton,
            addProjectButton1, submitAddTaskButton;

    @FXML
    private AnchorPane mainPane, opacityPane, drawerPane, mainOpacityPane, signInPane, signUpPane, successAlertPane,
            errorAlertPane;

    @FXML
    private StackPane loginWindow, addProjectWindow;

    @FXML
    private Text successAlert, errorAlert;

    @FXML
    private Button loginPageButton, signOutButton, signInSubmitButton, openSignUpPane, backToSignInPane,
            signUpSubmitButton, addProjectButton2, submitAddProjectButton, hideAddProjectWindow;

    @FXML
    private TextField usernameSignInInput, passwordSignInInput, usernameSignUpInput, passwordSignUpInput,
            newProjectName, newTaskName;

    @FXML
    private ComboBox<Project> newTaskSelectProject;

    @FXML
    private VBox taskListVbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        opacityPaneState = false;
        drawerPaneState = false;
        mainOpacityPane.setVisible(false);
        loginWindow.setVisible(false);
        initOpacityPane();

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), drawerPane);
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
                    } else if (addProjectWindow.isVisible()) {
                        hideAddProjectWindow();
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

        mainOpacityPane.setOnMouseClicked(event -> {
            mainOpacityPaneClickHandler();
        });

        setSignInEvents();
        setSignUpEvents();
        setAlertEvents();
        setAddProjectEvents();
        setAddTaskEvents();
    }

    private void setAddTaskEvents() {
        fillProjectComboBoxOptions();
        Tooltip.install(submitAddTaskButton, new Tooltip("Kliknij, aby dodać nowe zadanie"));
        submitAddTaskButton.setOnMouseClicked(event -> {
            submitAddTaskHandler();
        });

        newTaskSelectProject.valueProperty().addListener(new ChangeListener<Project>() {
            @Override
            public void changed(ObservableValue<? extends Project> observableValue, Project project, Project t1) {
                fillTaskElements();
            }
        });

        newTaskName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    submitAddTaskHandler();
                } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    newTaskName.clear();
                    clearProjectSelectComboBox();
                }
            }
        });

        newTaskSelectProject.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    submitAddProjectButton.requestFocus();
                    submitAddTaskHandler();
                } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    newTaskName.clear();
                    clearProjectSelectComboBox();
                }
            }
        });


        fillTaskElements();
    }

    public void clearProjectSelectComboBox() {
        newTaskSelectProject.setValue(null);
        newTaskSelectProject.setPromptText("Wybierz projekt");
        newTaskSelectProject.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setText("Wybierz projekt");
                } else {
                    this.setText(item.getName());
                }
            }
        });
    }

    public void fillTaskElements() {
        List<Task> tasks;
        TaskModel taskModel = new TaskModel();
        Project selectedProject = newTaskSelectProject.getSelectionModel().getSelectedItem();
        List<String> errors = new ArrayList<>();
        try {
            Integer userId = user != null ? user.id : -1;
            Integer projectId = selectedProject != null ? selectedProject.getId() : -1;
            tasks = taskModel.getTasks(userId, projectId);
            doFillTasks(tasks);
        } catch (DatabaseException e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void doFillTasks(List<Task> tasks) {
        List<TaskElement> taskElements = new ArrayList<>();
        for (Task task : tasks) {
            TaskElement taskElement = new TaskElement(
                    task.getId(),
                    task.getName(),
                    task.getDone(),
                    task.getDueDate(),
                    task.getProject(),
                    task.getNote(),
                    task.getCreatedAt()
            );
            taskElement.setId("taskElement" + task.getId());
            ImageView finishTask = (ImageView) taskElement.lookup("#finishTask");
            Label taskElementName = (Label) taskElement.lookup("#taskElementName");
            finishTask.setId("finishTask" + task.getId());
            taskElementName.setId("taskElementName" + task.getId());
            taskElementName.setText(task.getName());
            taskElements.add(taskElement);

            String newImageSource;
            if (taskElement.done) {
                newImageSource = String.valueOf(getClass().getResource("/images/task_done.png"));
            } else {
                newImageSource = String.valueOf(getClass().getResource("/images/task_to_do.png"));
            }
            finishTask.setImage(new Image(newImageSource));

            finishTask.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    ImageView source = ((ImageView)mouseEvent.getSource());
                    TaskElement taskElement = ((TaskElement)((ImageView)mouseEvent.getSource()).getParent());
                    String newImageSource;
                    if (taskElement.done) {
                        newImageSource = String.valueOf(getClass().getResource("/images/task_to_do.png"));
                    } else {
                        newImageSource = String.valueOf(getClass().getResource("/images/task_done.png"));
                    }
                    source.setImage(new Image(newImageSource));
                    taskElement.done = !taskElement.done;
                    updateTaskDone(taskElement.taskId, taskElement.done);
                }
            });
        }
        taskListVbox.getChildren().clear();
        if (taskElements.size() > 0) taskListVbox.getChildren().addAll(taskElements);
    }

    public void fillProjectComboBoxOptions() {
        ObservableList<Project> projectOptions = FXCollections.observableArrayList();
        List<Project> projects;
        ProjectModel projectModel = new ProjectModel();
        List<String> errors = new ArrayList<>();
        try {
            if (user == null) {
                projects = projectModel.getProjects();
            } else {
                projects = projectModel.getProjects(user.id);
            }
            projectOptions.addAll(projects);
            newTaskSelectProject.setItems(projectOptions);
            clearProjectSelectComboBox();
        } catch (DatabaseException e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
        newTaskSelectProject.setConverter(new StringConverter<Project>() {
            @Override
            public String toString(Project object) {
                return object.getName();
            }

            @Override
            public Project fromString(String string) {
                return newTaskSelectProject.getItems().stream().filter(project ->
                        project.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }

    private void updateTaskDone(Integer taskId, Boolean newDoneValue) {
        TaskModel taskModel = new TaskModel();
        List<String> errors = new ArrayList<>();
        try {
            Integer done = newDoneValue ? 1 : 0;
            taskModel.updateDone(taskId, done);
            showSuccessAlert("Pomyślnie zaktualizowano zadanie", 5);
            fillTaskElements();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void submitAddProjectHandler() {
        ProjectModel projectModel = new ProjectModel();
        String projectName = newProjectName.getText();
        List<String> errors = new ArrayList<>();
        try {
            if (user == null) {
                projectModel.storeProject(projectName);
            } else {
                projectModel.storeProject(projectName, user.id);
            }
            showSuccessAlert("Pomyślnie dodano projekt", 5);
            fillProjectComboBoxOptions();
            hideAddProjectWindow();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    public void submitAddTaskHandler() {
        TaskModel taskModel = new TaskModel();
        String taskName = newTaskName.getText();
        Project selectedProject = newTaskSelectProject.getSelectionModel().getSelectedItem();
        List<String> errors = new ArrayList<>();
        try {
            taskModel.storeTask(taskName, selectedProject);
            showSuccessAlert("Pomyślnie dodano zadanie", 5);
            hideAddProjectWindow();
            newTaskName.clear();
            clearProjectSelectComboBox();
            fillTaskElements();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void signUpHandler() {
        String username = usernameSignUpInput.getText();
        String password = passwordSignUpInput.getText();
        UserModel userModel = new UserModel();
        List<String> errors = new ArrayList<>();

        try {
            userModel.storeUser(username, password);
            showSuccessAlert("Pomyślnie utworzono użytkownika", 5);
            showSignInPane();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void setAddProjectEvents() {
        Tooltip.install(addProjectButton1, new Tooltip("Kliknij, aby dodać nowy projekt"));
        addProjectButton1.setOnMouseClicked(event -> {
            showAddProjectWindow();
        });
        addProjectButton2.setOnMouseClicked(event -> {
            showAddProjectWindow();
        });
        hideAddProjectWindow.setOnMouseClicked(event -> {
            hideAddProjectWindow();
        });
        submitAddProjectButton.setOnMouseClicked(event -> {
            submitAddProjectHandler();
        });

        newProjectName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    submitAddProjectButton.requestFocus();
                    submitAddProjectHandler();
                }
            }
        });
    }

    private void setAlertEvents() {
        closeSuccessAlertButton.setOnMouseClicked(event -> {
            hideSuccessAlert();
        });
        closeErrorAlertButton.setOnMouseClicked(event -> {
            hideErrorAlert();
        });
    }

    private void setSignUpEvents() {
        signUpPane.setVisible(false);

        openSignUpPane.setOnMouseClicked(event -> {
            showSignUpPane();
        });

        backToSignInPane.setOnMouseClicked(event -> {
            hideSignUpPane();
        });

        signUpSubmitButton.setOnMouseClicked(event -> {
            signUpHandler();
        });

        usernameSignUpInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    signUpSubmitButton.requestFocus();
                    signUpHandler();
                }
            }
        });

        passwordSignUpInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    signUpSubmitButton.requestFocus();
                    signUpHandler();
                }
            }
        });
    }

    private void showSignInPane() {
        usernameSignInInput.clear();
        passwordSignInInput.clear();
        hideSignUpPane();
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
        loginPageButton.setOnMouseClicked(event -> {
            showLoginWindow();
        });

        loginWindowClose.setOnMouseClicked(event -> {
            hideLoginWindow();
        });

        loginWindowClose2.setOnMouseClicked(event -> {
            hideLoginWindow();
        });

        signInSubmitButton.setOnMouseClicked(event -> {
            signInHandler();
        });

        signOutButton.setOnMouseClicked(event -> {
            signOutHandler();
        });

        usernameSignInInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    signInSubmitButton.requestFocus();
                    signInHandler();
                }
            }
        });

        passwordSignInInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    signInSubmitButton.requestFocus();
                    signInHandler();
                }
            }
        });
    }

    private void signOutHandler() {
        this.user = null;
        clearLoginWindowData();
        signOutButton.setVisible(false);
        loginPageButton.setVisible(true);
        fillProjectComboBoxOptions();
        showSuccessAlert("Wylogowano pomyślnie", 5);
    }

    private void signInHandler() {
        String username = usernameSignInInput.getText();
        String password = passwordSignInInput.getText();

        UserModel userModel = new UserModel();
        List<String> errors = new ArrayList<>();

        try {
            User user = userModel.authenticateUser(username, password);
            this.user = user;
            hideLoginWindow();
            loginPageButton.setVisible(false);
            signOutButton.setVisible(true);
            fillProjectComboBoxOptions();
            showSuccessAlert("Zalogowano pomyślnie", 5);
        } catch (DatabaseException | AuthenticationError | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void showLoginWindow() {
        signUpPane.setVisible(false);
        loginWindow.setVisible(true);
        signInPane.setVisible(true);
        usernameSignInInput.requestFocus();
        showOpacity(mainOpacityPane);
    }

    private void hideLoginWindow() {
        loginWindow.setVisible(false);
        clearLoginWindowData();
    }

    private void clearLoginWindowData() {
        usernameSignInInput.clear();
        passwordSignInInput.clear();
        usernameSignUpInput.clear();
        passwordSignUpInput.clear();
        hideMainOpacityPane();
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

    private void mainOpacityPaneClickHandler() {
//      todo: hide element depending on which window is currently open
        hideLoginWindow();
        hideAddProjectWindow();
    }

    private void hideMainOpacityPane() {
        FadeTransition fadeTransition = hideOpacity(mainOpacityPane, 0.01);
        fadeTransition.setOnFinished(event -> {
            mainOpacityPane.setVisible(false);
        });
    }

    public void showAddProjectWindow() {
        showOpacity(mainOpacityPane);
        addProjectWindow.setVisible(true);
        newProjectName.requestFocus();
    }

    public void hideAddProjectWindow() {
        addProjectWindow.setVisible(false);
        newProjectName.clear();
        hideMainOpacityPane();
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
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), drawerPane);
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

    private void showSuccessAlert(String message) {
        hideErrorAlert();
        doShowAlert(successAlertPane, successAlert, message, 0);
    }

    private void showSuccessAlert(String message, Integer duration) {
        // success alert with duration, after which it will disappear
        hideErrorAlert();
        doShowAlert(successAlertPane, successAlert, message, duration);
    }

    private void hideSuccessAlert() {
        doHideAlert(successAlertPane, successAlert);
    }

    private void showErrorAlert(String message) {
        hideSuccessAlert();
        doShowAlert(errorAlertPane, errorAlert, message, 0);
    }

    private void showErrorAlert(String message, Integer duration) {
        hideSuccessAlert();
        doShowAlert(errorAlertPane, errorAlert, message, duration);
    }

    private void hideErrorAlert() {
        doHideAlert(errorAlertPane, errorAlert);
    }

    private void doShowAlert(AnchorPane alertPane, Text alertTextBox, String message, double duration) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.1), alertPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(0.75);
        fadeTransition.setOnFinished(event -> {
            alertTextBox.setText(message);
            alertPane.setVisible(true);

            if (duration > 0) {
                PauseTransition pauseTransition = new PauseTransition(Duration.seconds(duration));
                pauseTransition.setOnFinished(event2 -> {
                    doHideAlert(alertPane, alertTextBox);
                });
                pauseTransition.play();
            }
        });
        fadeTransition.play();
    }

    private void doHideAlert(AnchorPane alertPane, Text alertTextBox) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), alertPane);
        fadeTransition.setFromValue(0.75);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(event -> {
            alertPane.setVisible(false);
            alertTextBox.setText("");
        });
        fadeTransition.play();
    }
}