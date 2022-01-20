package com.example.focustodoapp;

import com.example.focustodoapp.constants.Component;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class TaskElement extends AnchorPane {
    public Integer taskId;
    public String name;
    public Boolean done;
    public String dueDate;
    public Integer project;
    public String note;
    public String createdAt;

    public TaskElement(Integer taskId, String name, Boolean done, String dueDate, Integer project, String note, String createdAt) {
        this.taskId = taskId;
        this.name = name;
        this.done = done;
        this.dueDate = dueDate;
        this.project = project;
        this.note = note;
        this.createdAt = createdAt;
        String componentName = Component.getComponent(Component.TASK_ELEMENT);
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
