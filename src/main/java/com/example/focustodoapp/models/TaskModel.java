package com.example.focustodoapp.models;

import com.example.focustodoapp.constants.ErrorCode;
import com.example.focustodoapp.dtos.Project;
import com.example.focustodoapp.dtos.Task;
import com.example.focustodoapp.errors.DatabaseException;
import com.example.focustodoapp.errors.ValidationError;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskModel extends ModelInterface {
    private void checkTaskNameRequired(String taskName) throws ValidationError {
        if (Objects.equals(taskName.trim(), "")) throw new ValidationError("Nazwa zadania jest wymagana");
    }

    private void checkProjectRequired(Project project) throws ValidationError {
        if (project == null || project.getId() == -1) throw new ValidationError(
                "Aby utworzyć nowe zadanie, wymagane jest wybranie projektu");
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

    public List<Task> getTasks(Integer userId, Integer projectId) throws DatabaseException {
        try {
            String query;
            if (userId != -1) {
                query = "SELECT * FROM Task JOIN Project p on p.id = Task.project WHERE p.user = ?";
            } else {
                query = "SELECT * FROM Task JOIN Project p on p.id = Task.project WHERE p.user is NULL";
            }
            if (projectId != -1) query = query + " AND p.id = ?";
            query = query + " ORDER BY created_at DESC";
            PreparedStatement statement = getConnection().prepareStatement(query);

            if (userId != -1) {
                statement.setInt(1, userId);
                if (projectId != -1) statement.setInt(2, projectId);
            } else {
                if (projectId != -1) statement.setInt(1, projectId);
            }

            List<Task> tasks = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                tasks.add(
                    new Task(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3) == 1,
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getInt(7)
                    )
                );
            }
            closeConnection();
            return tasks;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się pobrać zadań, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    private void validateDoneValue(Integer newDoneValue) throws ValidationError {
        if (newDoneValue != 0 && newDoneValue != 1) throw new ValidationError("Nazwa zadania jest wymagana");
    }

    public void updateDone(Integer taskId, Integer newDoneValue) throws ValidationError, DatabaseException {
        validateDoneValue(newDoneValue);
        String query = "UPDATE Task SET done = ? WHERE id = ?";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1, newDoneValue);
            statement.setInt(2, taskId);
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się zaktualizować zadania, proszę spróbować ponownie",
                    ErrorCode.DB_ERROR
            );
        }
    }
}

