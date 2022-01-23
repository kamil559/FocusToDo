package com.example.focustodoapp.controllers;

import com.example.focustodoapp.ProjectElement;
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

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class MainController implements Initializable {
    private boolean opacityPaneState;
    private boolean drawerPaneState;
    private boolean taskStatsPaneState;
    private User user;
    private enum View {ALL, TODAY, TOMORROW, UPCOMING, DONE};
    private View currentView;
    private LineChart<String, Number> tasksLineChart;

    @FXML
    private ImageView drawerImage, loginWindowClose, loginWindowClose2, closeSuccessAlertButton, closeErrorAlertButton,
            addProjectButton1, submitAddTaskButton, allTasksBtn, todayTasksBtn, tomorrowTasksBtn, upcomingTasksBtn,
            doneTasksBtn, taskStatsBtn, exitTaskStatsPaneBtn;

    @FXML
    private AnchorPane mainPane, opacityPane, drawerPane, mainOpacityPane, signInPane, signUpPane, successAlertPane,
            errorAlertPane, addNewTaskPane, taskListPane, taskStatsPane, chartPlaceholder;

    @FXML
    private StackPane loginWindow, addProjectWindow, editTaskWindow, editProjectWindow, removeProjectPrompt,
            removeTaskPrompt;

    @FXML
    private Text successAlert, errorAlert;

    @FXML
    private Button loginPageButton, signOutButton, signInSubmitButton, openSignUpPane, backToSignInPane,
            signUpSubmitButton, addProjectButton2, submitAddProjectButton, hideAddProjectWindow, mainPaneHeader,
            cancelEditTaskButton, submitEditTaskButton, submitEditProjectButton, cancelEditProjectButton,
            removeProjectSubmitButton, removeTaskSubmitButton, removeProjectCancelButton, removeTaskCancelButton,
            allTasksView, todayTasksView, tomorrowTasksView, upcomingTasksView, doneTasksView;

    @FXML
    private TextField usernameSignInInput, passwordSignInInput, usernameSignUpInput, passwordSignUpInput,
            newProjectName, newTaskName, editTaskName, editTaskNote, editTaskId, editProjectId, editProjectName,
            removeProjectId, removeTaskId;

    @FXML
    private ComboBox<Project> newTaskSelectProject, editTaskProject;

    @FXML
    private VBox taskListVbox, projectListVbox;

    @FXML
    private DatePicker editTaskDueDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setOpacityPanesEvents();
        setMainPaneEvents();
        setMenuItemsEvents();
        setStatsPaneEvents();
        setLoginWindowEvents();
        setAlertEvents();
        setProjectEvents();
        setTaskEvents();
    }

    private void setOpacityPanesEvents() {
        opacityPaneState = false;
        mainOpacityPane.setVisible(false);
        initOpacityPane();

        opacityPane.setOnMouseClicked(event -> {
            opacityPaneClickHandler();
        });
    }

    private void setMainPaneEvents() {
        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    if (loginWindow.isVisible()) {
                        hideLoginWindow();
                    } else if (addProjectWindow.isVisible()) {
                        hideAddProjectWindow();
                    } else if (editTaskWindow.isVisible()) {
                        hideTaskEditWindow();
                    } else if (editProjectWindow.isVisible()) {
                        hideProjectEditWindow();
                    } else if (removeProjectPrompt.isVisible()) {
                        hideDeleteProjectPrompt();
                    } else if (removeTaskPrompt.isVisible()) {
                        hideDeleteTaskPrompt();
                    } else if (taskStatsPaneState) {
                        hideTaskStatsPane(false);
                    } else if (drawerPaneState) {
                        hideDrawer();
                        FadeTransition fadeTransition = hideOpacity(opacityPane);
                        fadeTransition.setOnFinished(event -> {
                            opacityPane.setVisible(false);
                            opacityPaneState = false;
                        });
                    }
                }

                if (newTaskName.isFocused()) {
                    taskListPane.requestFocus();
                }
            }
        });

        mainOpacityPane.setOnMouseClicked(event -> {
            mainOpacityPaneClickHandler();
        });
    }

    private void setMenuItemsEvents() {
        drawerPaneState = false;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), drawerPane);
        translateTransition.setByX(-600);
        translateTransition.play();
        drawerPane.setVisible(true);
        drawerImage.setOnMouseClicked(event -> {
            drawerImageClickHandler();
        });
        setCurrentView(View.ALL);
        allTasksView.setOnMouseClicked(event -> { setCurrentView(View.ALL); });
        allTasksBtn.setOnMouseClicked(event -> { setCurrentView(View.ALL); });
        todayTasksView.setOnMouseClicked(event -> { setCurrentView(View.TODAY); });
        todayTasksBtn.setOnMouseClicked(event -> { setCurrentView(View.TODAY); });
        tomorrowTasksView.setOnMouseClicked(event -> { setCurrentView(View.TOMORROW); });
        tomorrowTasksBtn.setOnMouseClicked(event -> { setCurrentView(View.TOMORROW); });
        upcomingTasksView.setOnMouseClicked(event -> { setCurrentView(View.UPCOMING); });
        upcomingTasksBtn.setOnMouseClicked(event -> { setCurrentView(View.UPCOMING); });
        doneTasksView.setOnMouseClicked(event -> { setCurrentView(View.DONE); });
        doneTasksBtn.setOnMouseClicked(event -> { setCurrentView(View.DONE); });
    }

    private void setStatsPaneEvents() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), taskStatsPane);
        translateTransition.setByX(+800);
        translateTransition.play();
        translateTransition.setOnFinished(event -> {
            taskStatsPane.setVisible(true);
        });

        Tooltip.install(taskStatsBtn, new Tooltip("Kliknij, aby wyświetlić statystyki zadań"));
        taskStatsBtn.setOnMouseClicked(event -> {
            if (taskStatsPaneState) {
                hideTaskStatsPane(false);
            } else {
                showTaskStatsPane();
            }
        });
        exitTaskStatsPaneBtn.setOnMouseClicked(event -> {
            hideTaskStatsPane(false);
        });
    }

    private void setLoginWindowEvents () {
        loginWindow.setVisible(false);
        setSignInEvents();
        setSignUpEvents();
    }

    private void setAlertEvents() {
        closeSuccessAlertButton.setOnMouseClicked(event -> {
            hideSuccessAlert();
        });
        closeErrorAlertButton.setOnMouseClicked(event -> {
            hideErrorAlert();
        });
    }

    private void setProjectEvents() {
        setAddProjectEvents();
        setEditProjectEvents();
        setRemoveProjectEvents();
    }

    private void setTaskEvents() {
        setAddTaskEvents();
        setEditTaskEvents();
        setRemoveTaskEvents();
    }

    private void setCurrentView(View currentView) {
        if (currentView == View.ALL) { markActiveView(allTasksView); }
        if (currentView == View.TODAY) { markActiveView(todayTasksView); }
        if (currentView == View.TOMORROW) { markActiveView(tomorrowTasksView); }
        if (currentView == View.UPCOMING) { markActiveView(upcomingTasksView); }
        if (currentView == View.DONE) { markActiveView(doneTasksView); }
    }

    private HashMap<Button, View> getButtonViewHashMap() {
        HashMap<Button, View> buttonViewHashMap = new HashMap<>();
        buttonViewHashMap.put(allTasksView, View.ALL);
        buttonViewHashMap.put(todayTasksView, View.TODAY);
        buttonViewHashMap.put(tomorrowTasksView, View.TOMORROW);
        buttonViewHashMap.put(upcomingTasksView, View.UPCOMING);
        buttonViewHashMap.put(doneTasksView, View.DONE);
        return buttonViewHashMap;
    }

    private void markActiveView(Button currentManuItem) {
        HashMap<Button, View> buttonViewHashMap = getButtonViewHashMap();
        currentView = buttonViewHashMap.get(currentManuItem);
        Button[] menuItems = {allTasksView, todayTasksView, tomorrowTasksView, upcomingTasksView, doneTasksView};
        for (Button menuItem : menuItems) { menuItem.setStyle("-fx-background-color: #fff"); }
        currentManuItem.setStyle("-fx-background-color:  #f5f5f5");
        fillTaskElements();
    }

    private List<Task> getDoneTasks() {
        List<Task> tasks;
        TaskModel taskModel = new TaskModel();
        List<String> errors = new ArrayList<>();

        try {
            Integer userId = user != null ? user.id : -1;
            tasks = taskModel.getTasksByDoneStatus(userId, -1, 1);
            return tasks;
        } catch (DatabaseException e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
        return new ArrayList<>();
    }

    private Map<String, List<Integer>> prepareSeriesData(String dateFromString) throws ParseException {
        Map<String, List<Integer>> tasksSeriesData = new TreeMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateToString = formatter.format(new Date());
        Date dateFrom = formatter.parse(dateFromString);
        Date dateTo = formatter.parse(dateToString);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateFrom);

        Calendar tomorrowCalendar = new GregorianCalendar();
        tomorrowCalendar.setTime(dateTo);
        tomorrowCalendar.add(Calendar.DATE, 1);
        Date beforeDate = tomorrowCalendar.getTime();

        while (calendar.getTime().before(beforeDate)) {
            Date result = calendar.getTime();
            tasksSeriesData.put(formatter.format(result), new ArrayList<>());
            calendar.add(Calendar.DATE, 1);
        }
        return tasksSeriesData;
    }

    private Map<String, Integer> getDailyDoneTasks() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Task> doneTasks = getDoneTasks();
        String fromDateString;

        if (doneTasks.size() > 0) {
            fromDateString = doneTasks.get(doneTasks.size() - 1).getCreatedAt();
        } else {
            fromDateString = formatter.format(new Date());
        }
        Map<String, List<Integer>> doneTasksSeriesData = prepareSeriesData(fromDateString);
        Map<String, Integer> dailyDoneTasksCountedSeriesData = new TreeMap<>();

        for (Task task : doneTasks) {
            String taskDate = formatter.format(formatter.parse(task.getDoneAt()));
            doneTasksSeriesData.get(taskDate).add(task.getId());
        }
        doneTasksSeriesData.forEach((key, values) -> {
            dailyDoneTasksCountedSeriesData.put(key, values.size());
        });
        return dailyDoneTasksCountedSeriesData;
    }

    private Map<String, Integer> getDailyNewTasks() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Task> newTasks = getTaskList(true);
        String fromDateString;
        if (newTasks.size() > 0) {
            fromDateString = newTasks.get(newTasks.size() - 1).getCreatedAt();
        } else {
            fromDateString = formatter.format(new Date());
        }
        Map<String, List<Integer>> newTasksSeriesData = prepareSeriesData(fromDateString);
        Map<String, Integer> dailyNewTasksCountedSeriesData = new TreeMap<>();

        for (Task task : newTasks) {
            String taskDate = formatter.format(formatter.parse(task.getCreatedAt()));
            newTasksSeriesData.get(taskDate).add(task.getId());
        }
        newTasksSeriesData.forEach((key, values) -> {
            dailyNewTasksCountedSeriesData.put(key, values.size());
        });
        return dailyNewTasksCountedSeriesData;
    }

    private void loadStatsGraph() throws ParseException {
        if (tasksLineChart != null) {
            chartPlaceholder.getChildren().remove(tasksLineChart);
        }

        XYChart.Series<String, Number> newTasksSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> doneTasksSeries = new XYChart.Series<>();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        tasksLineChart = new LineChart<>(xAxis,yAxis);
        tasksLineChart.setId("tasksLineChart");
        yAxis.setLabel("Liczba zadań");
        xAxis.setLabel("Dzień");
        newTasksSeries.setName("Nowe zadania");
        doneTasksSeries.setName("Ukończone zadania");

        Map<String, Integer> dailyDoneTasks = getDailyDoneTasks();
        Map<String, Integer> dailyNewTasks = getDailyNewTasks();
        dailyDoneTasks.forEach((doneAt, tasksCount) -> {
            doneTasksSeries.getData().add(new XYChart.Data<>(doneAt, tasksCount));
        });
        dailyNewTasks.forEach((createdAt, tasksCount) -> {
            newTasksSeries.getData().add(new XYChart.Data<>(createdAt, tasksCount));
        });
        List<Integer> tasksCount = new ArrayList<>(dailyNewTasks.values());  // yAxis upper limit
        int topTasksCount = tasksCount.stream().reduce(Integer.MIN_VALUE, Integer::max);
        int nearestRoundUpperBound = (topTasksCount + 5) - (topTasksCount % 5);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(nearestRoundUpperBound);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);

        AnchorPane.setTopAnchor(tasksLineChart, 0.0);
        AnchorPane.setBottomAnchor(tasksLineChart, 10.0);
        AnchorPane.setLeftAnchor(tasksLineChart, 10.0);
        AnchorPane.setRightAnchor(tasksLineChart, 10.0);
        tasksLineChart.getData().add(newTasksSeries);
        tasksLineChart.getData().add(doneTasksSeries);
        chartPlaceholder.getChildren().add(tasksLineChart);
    }

    private void showTaskStatsPane() {
        try {
            loadStatsGraph();
        } catch (ParseException e) {
            showErrorAlert("Nie udało się pobrać danych statystycznych");
        }

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), taskStatsPane);
        translateTransition.setByX(-800);
        translateTransition.play();
        translateTransition.setOnFinished(event -> {
            addNewTaskPane.setVisible(false);
            mainPaneHeader.setVisible(false);
            taskListPane.setVisible(false);
        });

        taskStatsPaneState = true;
    }

    private void hideTaskStatsPane(Boolean immediately) {
        if (immediately) {
            taskStatsPane.setVisible(false);
        } else {
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), taskStatsPane);
            translateTransition.setByX(+800);
            translateTransition.play();
        }
        mainPaneHeader.setVisible(true);
        taskListPane.setVisible(true);
        addNewTaskPane.setVisible(true);
        taskStatsPaneState = false;
    }

    private void setRemoveTaskEvents(){
        removeTaskSubmitButton.setOnMouseClicked(event -> {
            removeTaskSubmitHandler();
        });

        removeTaskCancelButton.setOnMouseClicked(event -> {
            hideDeleteTaskPrompt();
        });
    }

    private void setEditTaskEvents() {
        cancelEditTaskButton.setOnMouseClicked(event -> {
            hideTaskEditWindow();
        });

        submitEditTaskButton.setOnMouseClicked(event -> {
            taskEditSaveHandler();
        });
    }

    private void setAddTaskEvents() {
        fillProjectComboBoxOptions(newTaskSelectProject);
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
                    clearProjectSelectComboBox(newTaskSelectProject);
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
                    clearProjectSelectComboBox(newTaskSelectProject);
                }
            }
        });
        fillTaskElements();
    }

    public void clearProjectSelectComboBox(ComboBox<Project> comboBox) {
        comboBox.setValue(null);
        comboBox.setPromptText("Wybierz projekt");
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setText("Wybierz projekt");
                } else {
                    this.setText(item != null ? item.getName() : "Wybierz projekt");
                }
            }
        });
    }

    public void fillProjectElements() {
        List<Project> projects;
        ProjectModel projectModel = new ProjectModel();
        List<String> errors = new ArrayList<>();
        try {
            if (user == null) {
                projects = projectModel.getProjects();
            } else {
                projects = projectModel.getProjects(user.id);
            }
            doFillProjects(projects);
        } catch (DatabaseException e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    public List<Task> getTaskList(Boolean ignoreProject) {
        TaskModel taskModel = new TaskModel();
        Project selectedProject = newTaskSelectProject.getSelectionModel().getSelectedItem();
        List<String> errors = new ArrayList<>();
        try {
            Integer userId = user != null ? user.id : -1;
            Integer projectId = selectedProject != null && !ignoreProject ? selectedProject.getId() : -1;
            return taskModel.getAllTasks(userId, projectId);
        } catch (DatabaseException e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
        return new ArrayList<>();
    }

    public void fillTaskElements() {
        List<Task> tasks;
        Project selectedProject = newTaskSelectProject.getSelectionModel().getSelectedItem();
        Integer userId = user != null ? user.id : -1;
        Integer projectId = selectedProject != null ? selectedProject.getId() : -1;
        TaskModel taskModel = new TaskModel();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> errors = new ArrayList<>();
        try {
            if (currentView == View.TODAY) {
                String date = dateFormat.format(new Date());
                tasks = taskModel.getTasksForDate(userId, projectId, date);
            } else if (currentView == View.TOMORROW) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 1);
                String date = dateFormat.format(calendar.getTime());
                tasks = taskModel.getTasksForDate(userId, projectId, date);
            } else if (currentView == View.UPCOMING) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 2);
                String date = dateFormat.format(calendar.getTime());
                tasks = taskModel.getUpcomingTasks(userId, projectId, date);
            } else if (currentView == View.DONE) {
                tasks = taskModel.getTasksByDoneStatus(userId, projectId, 1);
            } else {
                tasks = getTaskList(false);
            }
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

    private void doFillProjects(List<Project> projects) {
        List<ProjectElement> projectElements = new ArrayList<>();
        for (Project project : projects) {
            ProjectElement projectElement = new ProjectElement(
                    project.getId(),
                    project.getName(),
                    project.getUser(),
                    project.getCreatedAt(),
                    project.getTasksCount(),
                    project.getTasksDone()
            );
            projectElement.setId("projectElement" + project.getId());
            Label projectNameLabel = (Label) projectElement.lookup("#projectName");
            Label projectTaskCounter = (Label) projectElement.lookup("#projectTaskCounter");
            projectNameLabel.setId("projectName" + project.getId());
            projectNameLabel.setId("projectTaskCounter" + project.getId());
            projectNameLabel.setText(projectElement.name);
            String totalTasks = projectElement.tasksCount.toString();
            String doneTasks = projectElement.tasksDone.toString();
            String tasksCounter = doneTasks + "/" + totalTasks;
            projectTaskCounter.setText(tasksCounter);
            Tooltip.install(projectNameLabel, new Tooltip(project.getName()));
            Tooltip.install(projectTaskCounter, new Tooltip("Liczba wykonanych i przypisanych zadań: " + tasksCounter));
            projectElements.add(projectElement);

            ContextMenu contextMenu = new ContextMenu();
            MenuItem itemEdit = new MenuItem("Edytuj");
            MenuItem itemDelete = new MenuItem("Usuń");
            contextMenu.getItems().addAll(itemEdit, itemDelete);
            contextMenu.setId("projectElementContextMenu" + project.getId());
            itemEdit.setId("projectElementContextMenuEdit" + project.getId());
            itemDelete.setId("projectElementContextMenuDelete" + project.getId());
            itemEdit.setUserData(projectElement.projectId);
            itemDelete.setUserData(projectElement.projectId);
            contextMenu.setMinHeight(50);
            contextMenu.setMinWidth(90);
            contextMenu.setStyle("-fx-border-width: 0; -fx-border-radius: 5px; -fx-background-radius: 5px");
            itemEdit.setStyle("-fx-font-size: 16px");
            itemDelete.setStyle("-fx-font-size: 16px");

            itemEdit.setOnAction(event -> {
                Integer projectId = (Integer) ((MenuItem) event.getSource()).getUserData();
                showProjectEditWindow(projectId);
            });

            itemDelete.setOnAction(event -> {
                Integer projectId = (Integer) ((MenuItem) event.getSource()).getUserData();
                showDeleteProjectPrompt(projectId);
            });

            projectElement.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent contextMenuEvent) {
                    contextMenu.show(projectElement, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                }
            });
        }
        projectListVbox.getChildren().clear();
        if (projectElements.size() > 0) projectListVbox.getChildren().addAll(projectElements);
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
            ImageView deleteTask = (ImageView) taskElement.lookup("#deleteTask");
            Label taskElementName = (Label) taskElement.lookup("#taskElementName");
            AnchorPane taskEditWindowOpener = (AnchorPane) taskElement.lookup("#taskEditWindowOpener");
            finishTask.setId("finishTask" + task.getId());
            deleteTask.setId("deleteTask" + task.getId());
            deleteTask.setUserData(task.getId());
            taskElementName.setId("taskElementName" + task.getId());
            taskEditWindowOpener.setId("taskEditWindowOpener" + task.getId());
            taskElementName.setText(task.getName());
            taskElements.add(taskElement);

            String newImageSource;
            if (taskElement.done) {
                newImageSource = String.valueOf(getClass().getResource("/images/task_done.png"));
            } else {
                newImageSource = String.valueOf(getClass().getResource("/images/task_to_do.png"));
            }
            finishTask.setImage(new Image(newImageSource));

            taskEditWindowOpener.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() != MouseButton.SECONDARY) {
                        TaskElement taskElement = ((TaskElement)((AnchorPane)mouseEvent.getSource()).getParent());
                        showTaskEditWindow(taskElement);
                    }
                    mouseEvent.consume();
                }
            });

            finishTask.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() != MouseButton.SECONDARY) {
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
                }
            });

            deleteTask.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() != MouseButton.SECONDARY) {
                        Integer taskId = (Integer) ((ImageView) mouseEvent.getSource()).getUserData();
                        if (taskId != null) { showDeleteTaskPrompt(taskId); }
                    }
                }
            });
        }
        taskListVbox.getChildren().clear();
        if (taskElements.size() > 0) taskListVbox.getChildren().addAll(taskElements);
    }

    public void fillProjectComboBoxOptions(ComboBox<Project> comboBox) {
        ObservableList<Project> projectOptions = FXCollections.observableArrayList();
        projectOptions.add(null);
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
            comboBox.setItems(projectOptions);
            clearProjectSelectComboBox(comboBox);
        } catch (DatabaseException e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
        comboBox.setConverter(new StringConverter<Project>() {
            @Override
            public String toString(Project object) {
                return object.getName();
            }

            @Override
            public Project fromString(String string) {
                return comboBox.getItems().stream().filter(project ->
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
            fillProjectElements();
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
            fillProjectComboBoxOptions(newTaskSelectProject);
            fillProjectComboBoxOptions(editTaskProject);
            fillProjectElements();
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
            clearProjectSelectComboBox(newTaskSelectProject);
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
            usernameSignInInput.setText(username);
            passwordSignInInput.requestFocus();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void setEditProjectEvents() {
        fillProjectElements();

        cancelEditProjectButton.setOnMouseClicked(event -> {
            hideProjectEditWindow();
        });

        submitEditProjectButton.setOnMouseClicked(event -> {
            projectEditSaveHandler();
        });
    }

    private void setRemoveProjectEvents() {
        removeProjectSubmitButton.setOnMouseClicked(event -> {
            removeProjectSubmitHandler();
        });

        removeProjectCancelButton.setOnMouseClicked(event -> {
            hideDeleteProjectPrompt();
        });
    }

    public void projectEditSaveHandler() {
        ProjectModel projectModel = new ProjectModel();
        Integer projectId = Integer.parseInt(editProjectId.getText());
        String projectName = editProjectName.getText();

        List<String> errors = new ArrayList<>();
        Integer userId = user != null ? user.id : -1;
        try {
            projectModel.updateProject(userId, projectId, projectName);
            showSuccessAlert("Pomyślnie zaktualizowano projekt", 5);
            fillProjectElements();
            fillProjectComboBoxOptions(newTaskSelectProject);
            fillProjectComboBoxOptions(editTaskProject);
            hideProjectEditWindow();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void showProjectEditWindow(Integer projectId) {
        Project project = getProject(projectId);
        if (project != null) {
            showOpacity(mainOpacityPane);
            loadProjectDataToEditWindow(project);
            editProjectWindow.setVisible(true);
        }
    }

    private void loadProjectDataToEditWindow(Project project) {
        editProjectId.setText(project.getId().toString());
        editProjectName.setText(project.getName());
    }

    private void hideProjectEditWindow() {
        hideMainOpacityPane();
        editProjectWindow.setVisible(false);
        clearEditProjectWindowInputs();
    }

    private void clearEditProjectWindowInputs() {
        editProjectId.clear();
        editProjectName.clear();
    }

    private void showDeleteTaskPrompt(Integer taskId) {
        showOpacity(mainOpacityPane);
        removeTaskId.setText(taskId.toString());
        removeTaskPrompt.setVisible(true);
    }

    private void hideDeleteTaskPrompt() {
        hideMainOpacityPane();
        removeTaskId.clear();
        removeTaskPrompt.setVisible(false);
    }

    private void removeTaskSubmitHandler() {
        TaskModel taskModel = new TaskModel();
        Integer taskId = Integer.parseInt(removeTaskId.getText());

        List<String> errors = new ArrayList<>();
        Integer userId = user != null ? user.id : -1;
        try {
            taskModel.deleteTask(userId, taskId);
            showSuccessAlert("Zadanie zostało usunięte", 5);
            fillProjectElements();
            fillTaskElements();
            hideDeleteTaskPrompt();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    private void showDeleteProjectPrompt(Integer projectId) {
        showOpacity(mainOpacityPane);
        removeProjectId.setText(projectId.toString());
        removeProjectPrompt.setVisible(true);
    }

    private void hideDeleteProjectPrompt() {
        hideMainOpacityPane();
        removeProjectId.clear();
        removeProjectPrompt.setVisible(false);
    }

    private void removeProjectSubmitHandler() {
        ProjectModel projectModel = new ProjectModel();
        Integer projectId = Integer.parseInt(removeProjectId.getText());

        List<String> errors = new ArrayList<>();
        Integer userId = user != null ? user.id : -1;
        try {
            projectModel.deleteProject(userId, projectId);
            showSuccessAlert("Projekt został usunięty", 5);
            fillProjectElements();
            fillProjectComboBoxOptions(newTaskSelectProject);
            fillProjectComboBoxOptions(editTaskProject);
            hideDeleteProjectPrompt();
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

        editProjectName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    submitEditProjectButton.requestFocus();
                    projectEditSaveHandler();
                }
            }
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
        fillProjectComboBoxOptions(newTaskSelectProject);
        fillProjectComboBoxOptions(editTaskProject);
        fillTaskElements();
        fillProjectElements();
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
            fillProjectComboBoxOptions(newTaskSelectProject);
            fillProjectComboBoxOptions(editTaskProject);
            fillTaskElements();
            fillProjectElements();
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
            fillProjectElements();
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
        hideTaskEditWindow();
        hideProjectEditWindow();
        hideDeleteProjectPrompt();
        hideDeleteTaskPrompt();
    }

    private void hideMainOpacityPane() {
        FadeTransition fadeTransition = hideOpacity(mainOpacityPane, 0.01);
        fadeTransition.setOnFinished(event -> {
            mainOpacityPane.setVisible(false);
        });
    }

    private LocalDate getDateFromString(String date) {
        return LocalDate.parse(date);
    }

    private Project getProject(Integer projectId) {
        ProjectModel projectModel = new ProjectModel();
        List<String> errors = new ArrayList<>();
        try {
            return projectModel.getProject(projectId);
        } catch (DatabaseException e) {
            errors.add(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
        return null;
    }

    public void loadTaskDataToEditWindow(TaskElement task) {
        editTaskId.setText(task.taskId.toString());
        editTaskName.setText(task.name);
        if (task.dueDate != null && !task.dueDate.equals("")) {
            editTaskDueDate.setValue(getDateFromString(task.dueDate));
        }
        fillProjectComboBoxOptions(editTaskProject);
        Project selectedProject = getProject(task.project);
        editTaskProject.getSelectionModel().select(selectedProject);
        editTaskNote.setText(task.note != null ? task.note : "");
    }

    public void taskEditSaveHandler() {
        TaskModel taskModel = new TaskModel();
        Integer taskId = Integer.parseInt(editTaskId.getText());
        String taskName = editTaskName.getText();
        LocalDate localDateDueDate = editTaskDueDate.getValue();
        String dueDate = localDateDueDate != null ? localDateDueDate.toString() : null;
        Project project = editTaskProject.getSelectionModel().getSelectedItem();
        String note = editTaskNote.getText();

        List<String> errors = new ArrayList<>();
        Integer userId = user != null ? user.id : -1;
        try {
            taskModel.updateTask(userId, taskId, taskName, dueDate, project, note);
            showSuccessAlert("Pomyślnie zaktualizowano zadanie", 5);
            fillTaskElements();
            hideTaskEditWindow();
        } catch (DatabaseException | ValidationError e) {
            errors.add(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (!errors.isEmpty()) {
                String joinedErrors = String.join("\n", errors);
                showErrorAlert(joinedErrors, 10);
            }
        }
    }

    public void showTaskEditWindow(TaskElement task) {
        showOpacity(mainOpacityPane);
        loadTaskDataToEditWindow(task);
        editTaskWindow.setVisible(true);
    }

    private void clearEditTaskWindowInputs() {
        editTaskName.clear();
        editTaskDueDate.setValue(null);
        clearProjectSelectComboBox(editTaskProject);
        editTaskNote.clear();
    }

    public void hideTaskEditWindow() {
        hideMainOpacityPane();
        editTaskWindow.setVisible(false);
        clearEditTaskWindowInputs();
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