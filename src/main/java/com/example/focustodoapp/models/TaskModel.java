package com.example.focustodoapp.models;

import com.example.focustodoapp.constants.ErrorCode;
import com.example.focustodoapp.dtos.Project;
import com.example.focustodoapp.errors.DatabaseException;
import com.example.focustodoapp.errors.ValidationError;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class TaskModel extends ModelInterface {
    private void checkTaskNameRequired(String taskName) throws ValidationError {
        if (Objects.equals(taskName.trim(), "")) throw new ValidationError("Nazwa zadania jest wymagana");
    }

    private void checkProjectRequired(Project project) throws ValidationError {
        if (project == null) throw new ValidationError("Wymagane jest wybranie projektu");
    }

    public void storeTask(String taskName, Project project) throws ValidationError, DatabaseException {
        checkTaskNameRequired(taskName);
        checkProjectRequired(project);
        String query = "INSERT INTO Task (name, project) VALUES (?, ?)";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, taskName);
            statement.setInt(2, project.getId());
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się utworzyć nowego zadania, proszę spróbować ponownie",
                    ErrorCode.DB_ERROR
            );
        }
    }
}

