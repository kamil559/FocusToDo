package com.example.focustodoapp.models;

import com.example.focustodoapp.constants.ErrorCode;
import com.example.focustodoapp.errors.DatabaseException;
import com.example.focustodoapp.errors.ValidationError;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class ProjectModel extends ModelInterface {
    private void checkProjectNameRequired(String projectName) throws ValidationError {
        if (Objects.equals(projectName.trim(), "")) throw new ValidationError("Nazwa projektu jest wymagana");
    }

    public void storeProject(String projectName) throws DatabaseException, ValidationError {
        checkProjectNameRequired(projectName);
        String query = "INSERT INTO Project (name) VALUES (?)";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, projectName);
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się utworzyć nowego projektu, proszę spróbować ponownie",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public void storeProject(String projectName, Integer userId) throws DatabaseException, ValidationError {
        checkProjectNameRequired(projectName);
        String query = "INSERT INTO Project (name, user) VALUES (?, ?)";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, projectName);
            statement.setInt(2, userId);
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się utworzyć nowego projektu, proszę spróbować ponownie",
                    ErrorCode.DB_ERROR
            );
        }
    }
}

