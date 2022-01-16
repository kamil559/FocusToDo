package com.example.focustodoapp.constants;

import java.util.Map;

public enum Component {
    LOGIN,
    MAIN;

    public static String getComponent(Component component) {
        Map<Component, String> components = Map.ofEntries(
                Map.entry(Component.LOGIN, "login-view.fxml"),
                Map.entry(Component.MAIN, "main-view.fxml")
        );
        return components.get(component);
    }
}
