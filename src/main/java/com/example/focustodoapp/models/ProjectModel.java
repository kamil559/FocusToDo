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

    public List<Project> getProjects() throws DatabaseException {
        String query = "SELECT id, name, user, created_at FROM Project WHERE user IS NULL ORDER BY created_at DESC";
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
                                resultSet.getString(4)
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
        String query = "SELECT id, name, user, created_at FROM Project WHERE user = ? ORDER BY created_at DESC";
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
                                resultSet.getString(4)
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
            return project;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }
}

