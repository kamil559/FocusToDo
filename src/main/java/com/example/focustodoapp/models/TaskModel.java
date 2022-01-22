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

    private void checkProjectRequired(Project project, Boolean beingCreated) throws ValidationError {
        if (project == null || project.getId() == -1) throw new ValidationError(
                beingCreated ? "Aby utworzyć nowe zadanie, wymagane jest wybranie projektu" :
                        "Aby zaktualizować zadanie, wymagane jest wybranie projektu"
        );
    }

    private void checkProjectBelongsToUser(Integer userId, Project project) throws ValidationError, SQLException {
        try {
            String query = "SELECT COUNT(*) FROM Project WHERE id = ?";

            if (userId != -1) {
                query = query + " AND user = ?";
            } else {
                query = query + " AND user IS NULL";
            }

            PreparedStatement statement = getConnection().prepareStatement(query);

            statement.setInt(1, project.getId());
            if (userId != -1) {
                statement.setInt(2, userId);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.getInt(1) == 0) throw new ValidationError("Nie możesz edytować tego zadania");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }


    public void storeTask(String taskName, Project project) throws ValidationError, DatabaseException {
        checkTaskNameRequired(taskName);
        checkProjectRequired(project, true);
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

    public List<Task> getDoneTasks(Integer userId, Integer projectId) throws DatabaseException {
        try {
            String query;
            if (userId != -1) {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE t.done = 1 AND p.user = ?";
            } else {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE t.done = 1 AND p.user is NULL";
            }
            if (projectId != -1) query = query + " AND p.id = ?";
            query = query + " ORDER BY t.created_at DESC";
            PreparedStatement statement = getConnection().prepareStatement(query);

            if (userId != -1) {
                statement.setInt(1, userId);
                if (projectId != -1) statement.setInt(2, projectId);
            } else {
                if (projectId != -1) statement.setInt(1, projectId);
            }
            return getTasks(statement);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się pobrać zadań, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public List<Task> getAllTasks(Integer userId, Integer projectId) throws DatabaseException {
        try {
            String query;
            if (userId != -1) {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE p.user = ?";
            } else {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE p.user is NULL";
            }
            if (projectId != -1) query = query + " AND p.id = ?";
            query = query + " ORDER BY t.created_at DESC";
            PreparedStatement statement = getConnection().prepareStatement(query);

            if (userId != -1) {
                statement.setInt(1, userId);
                if (projectId != -1) statement.setInt(2, projectId);
            } else {
                if (projectId != -1) statement.setInt(1, projectId);
            }
            return getTasks(statement);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się pobrać zadań, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public List<Task> getTasksForDate(Integer userId, Integer projectId, String date) throws DatabaseException {
        try {
            String query;
            if (userId != -1) {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE t.due_date = ? AND p.user = ?";
            } else {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE t.due_date = ? AND p.user is NULL";
            }
            if (projectId != -1) query = query + " AND p.id = ?";
            query = query + " ORDER BY t.created_at DESC";
            PreparedStatement statement = getConnection().prepareStatement(query);

            statement.setString(1, date);
            if (userId != -1) {
                statement.setInt(2, userId);
                if (projectId != -1) statement.setInt(3, projectId);
            } else {
                if (projectId != -1) statement.setInt(2, projectId);
            }
            return getTasks(statement);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się pobrać zadań, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public List<Task> getUpcomingTasks(Integer userId, Integer projectId, String date) throws DatabaseException {
        try {
            String query;
            if (userId != -1) {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE t.due_date >= date(?) AND p.user = ?";
            } else {
                query = "SELECT t.id, t.name, t.done, t.due_date, t.project, t.note, t.created_at FROM Task t JOIN Project p on p.id = t.project WHERE t.due_date >= date(?) AND p.user is NULL";
            }
            if (projectId != -1) query = query + " AND p.id = ?";
            query = query + " ORDER BY t.created_at DESC";
            PreparedStatement statement = getConnection().prepareStatement(query);

            statement.setString(1, date);
            if (userId != -1) {
                statement.setInt(2, userId);
                if (projectId != -1) statement.setInt(3, projectId);
            } else {
                if (projectId != -1) statement.setInt(2, projectId);
            }
            return getTasks(statement);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się pobrać zadań, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public List<Task> getTasks(PreparedStatement statement) throws DatabaseException {
        try {
            List<Task> tasks = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                tasks.add(
                    new Task(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3) == 1,
                        resultSet.getString(4),
                            resultSet.getInt(5),
                            resultSet.getString(6),
                            resultSet.getString(7)
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

    public void updateTask(Integer requesterId, Integer taskId, String taskName, String dueDate, Project project,
                           String note)
            throws ValidationError, SQLException {
        try {
            checkTaskNameRequired(taskName);
            checkProjectRequired(project, false);
            checkProjectBelongsToUser(requesterId, project);
            String query = "UPDATE Task SET name = ?, due_date = ?, project = ?, note = ? WHERE id = ?";
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, taskName);
            statement.setString(2, dueDate);
            statement.setInt(3, project.getId());
            statement.setString(4, note);
            statement.setInt(5, taskId);
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się zaktualizować zadania, proszę spróbować ponownie",
                    ErrorCode.DB_ERROR
            );
        }
    }

    private void checkTaskBelongsToUser(Integer userId, Integer taskId)
            throws ValidationError, SQLException {
        String query;
        try{
            if (userId != -1) {
                query = "SELECT COUNT(*) FROM Task t JOIN Project P on t.project = P.id WHERE t.id = ? AND p.user = ?";
            } else {
                query = "SELECT COUNT(*) FROM Task t JOIN Project P on t.project = P.id WHERE t.id = ? AND p.user IS NULL";
            }
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1, taskId);

            if (userId != -1) {
                statement.setInt(2, userId);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.getInt(1) == 0) throw new ValidationError("Nie możesz usunąć tego zadania");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void deleteTask(Integer requesterId, Integer taskId) throws ValidationError, DatabaseException {
        try {
            checkTaskBelongsToUser(requesterId, taskId);
            String query = "DELETE FROM Task WHERE id = ?";
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1, taskId);
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się usunąć projektu, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }
}

