package com.example.focustodoapp.constants;

import java.util.Map;

public enum Component {
    LOGIN,
    MAIN,
    TASK_ELEMENT,
    PROJECT_ELEMENT;

    public static String getComponent(Component component) {
        Map<Component, String> components = Map.ofEntries(
                Map.entry(Component.LOGIN, "login-view.fxml"),
                Map.entry(Component.MAIN, "main-view.fxml"),
                Map.entry(Component.TASK_ELEMENT, "task-element.fxml"),
                Map.entry(Component.PROJECT_ELEMENT, "project-element.fxml")
        );
        return components.get(component);
    }
}
