package com.example.focustodoapp;

import com.example.focustodoapp.constants.Component;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ProjectElement extends AnchorPane {
    public Integer projectId;
    public String name;
    public Integer userId;
    public String createdAt;
    public Integer tasksCount;
    public Integer tasksDone;

    public ProjectElement(Integer projectId, String name, Integer userId, String createdAt, Integer tasksCount,
                          Integer tasksDone) {
        this.projectId = projectId;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.tasksCount = tasksCount;
        this.tasksDone = tasksDone;
        String componentName = Component.getComponent(Component.PROJECT_ELEMENT);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(componentName));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
