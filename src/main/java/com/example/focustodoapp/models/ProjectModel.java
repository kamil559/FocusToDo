package com.example.focustodoapp.models;

import com.example.focustodoapp.constants.ErrorCode;
import com.example.focustodoapp.dtos.Project;
import com.example.focustodoapp.errors.DatabaseException;
import com.example.focustodoapp.errors.ValidationError;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                    "Nie udało się utworzyć nowego projektu, proszę spróbować ponownie później",
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
                    "Nie udało się utworzyć nowego projektu, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public List<Project> getProjects() throws DatabaseException {
        String query = "SELECT p.id, p.name, p.user, p.created_at, COUNT(t.id) tasks_count, SUM(t.done) tasks_done " +
                "FROM Project p LEFT JOIN Task t ON p.id = t.project WHERE user IS NULL " +
                "GROUP BY p.id, p.name, p.user, p.created_at ORDER BY p.created_at DESC";
        List<Project> projects = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                projects.add(
                        new Project(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getInt(3),
                                resultSet.getString(4),
                                resultSet.getInt(5),
                                resultSet.getInt(6)
                        )
                );
            }
            closeConnection();
            return projects;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się wykonać tej operacji, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public List<Project> getProjects(Integer userId) throws DatabaseException {
        String query = "SELECT p.id, p.name, p.user, p.created_at, COUNT(t.id) tasks_count, SUM(t.done) tasks_done " +
                "FROM Project p LEFT JOIN Task t ON p.id = t.project WHERE user = ? " +
                "GROUP BY p.id, p.name, p.user, p.created_at ORDER BY p.created_at DESC";
        List<Project> projects = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                projects.add(
                        new Project(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getInt(3),
                                resultSet.getString(4),
                                resultSet.getInt(5),
                                resultSet.getInt(6)
                        )
                );
            }
            closeConnection();
            return projects;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się wykonać tej operacji, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public Project getProject(Integer projectId) throws SQLException {
        String query = "SELECT id, name, user, created_at FROM Project WHERE id = ? ORDER BY created_at DESC";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            Project project = new Project(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getString(4)
            );
            closeConnection();
            return project;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkProjectBelongsToUser(Integer userId, Integer projectId, String errorString)
            throws ValidationError, SQLException {
        String query;
        try{
            if (userId != -1) {
                query = "SELECT COUNT(*) FROM Project WHERE id = ? AND user = ?";
            } else {
                query = "SELECT COUNT(*) FROM Project WHERE id = ? AND user IS NULL";
            }
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1, projectId);

            if (userId != -1) {
                statement.setInt(2, userId);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.getInt(1) == 0) throw new ValidationError(errorString);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void updateProject(Integer requesterId, Integer projectId, String projectName) throws
            ValidationError, DatabaseException {
        try {
            checkProjectNameRequired(projectName);
            checkProjectBelongsToUser(requesterId, projectId, "Nie możesz edytować tego projektu");
            String query = "UPDATE Project SET name = ? WHERE id = ?";
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, projectName);
            statement.setInt(2, projectId);
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się zaktualizować projektu, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public void deleteProject(Integer requesterId, Integer projectId) throws ValidationError, DatabaseException {
        try {
            checkProjectBelongsToUser(requesterId, projectId, "Nie możesz usunąć tego projektu");
            String query = "DELETE FROM Project WHERE id = ?";
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1, projectId);
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

