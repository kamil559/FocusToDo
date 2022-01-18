package com.example.focustodoapp.models;

import com.example.focustodoapp.constants.ErrorCode;
import com.example.focustodoapp.dtos.AuthUser;
import com.example.focustodoapp.dtos.User;
import com.example.focustodoapp.errors.AuthenticationError;
import com.example.focustodoapp.errors.DatabaseException;
import com.example.focustodoapp.errors.ValidationError;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class UserModel extends ModelInterface {
    public boolean userExists(String username) throws DatabaseException {
        String query = "SELECT EXISTS(SELECT * FROM User WHERE UPPER(username) like ?) user_exists";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            int userExists = resultSet.getInt(1);  // either 0 or 1
            closeConnection();
            return userExists == 1;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się wykonać tej operacji, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public boolean userExists(String username, Integer existingUserId) throws DatabaseException {
        // Custom utility method for user edit purposes
        String query = "SELECT COUNT(*) users_count FROM User WHERE UPPER(username) like ? AND id != ?;";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, existingUserId.toString());
            ResultSet resultSet = statement.executeQuery();
            int userExists = resultSet.getInt(1);  // either 0 or 1
            return userExists == 1;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się wykonać tej operacji, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public User getUser(Integer userId) throws DatabaseException {
        String query = "SELECT id, username, first_name, last_name FROM User WHERE id = ?";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, userId.toString());
            ResultSet resultSet = statement.executeQuery();
            User user = new User(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            );
            closeConnection();
            return user;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się wykonać tej operacji, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public AuthUser getAuthUser(String username) throws DatabaseException {
        String query = "SELECT id, username, password FROM User WHERE UPPER(username) LIKE ?";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            AuthUser authUser = new AuthUser(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3)
            );
            closeConnection();
            return authUser;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się wykonać tej operacji, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public User getUser(String username) throws DatabaseException {
        String query = "SELECT id, username, first_name, last_name FROM User WHERE UPPER(username) LIKE ?";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            User user = new User(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            );
            closeConnection();
            return user;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Nie udało się wykonać tej operacji, proszę spróbować ponownie później",
                    ErrorCode.DB_ERROR
            );
        }
    }

    private void checkUsernameUniqueness(String username) throws DatabaseException, ValidationError {
        boolean userExists = userExists(username);
        if (userExists) throw new ValidationError("Użytkownik o podanym loginie już istnieje");
    }

    private void validatePassword(String password) throws ValidationError {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        boolean isPasswordValid = pattern.matcher(password).matches();
        if (!isPasswordValid) {
            throw new ValidationError("Podane hasło jest zbyt słabe, prawidłowe hasło powinno składać się z: \n" +
                    "-minimum jednej cyfry,\n" +
                    "-minimum jednej małej i wielkiej litery,\n" +
                    "-minimum jednego znaku specjalnego,\n" +
                    "-minimum 8 znaków łącznie");
        }
    }

    private void checkPasswordMatchesUser(AuthUser user, String passwordCandidate) throws AuthenticationError {
        boolean passwordMatches = BCrypt.checkpw(passwordCandidate, user.password);
        if (!passwordMatches) throw new AuthenticationError("Podano nieprawidłowe hasło");
    }

    private void checkUsernameRequired(String username) throws ValidationError {
        if (Objects.equals(username, "")) throw new ValidationError("Login jest wymagany");
    }

    private void checkPasswordRequired(String password) throws ValidationError {
        if (Objects.equals(password, "")) throw new ValidationError("Hasło jest wymagane");
    }

    public void storeUser(String username, String password) throws DatabaseException, ValidationError {
        checkUsernameRequired(username);
        checkPasswordRequired(password);
        checkUsernameUniqueness(username);
        validatePassword(password);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(5));
        String query = "INSERT INTO User (username, password) VALUES (?, ?)";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.executeUpdate();
            closeConnection();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Proces rejestracji konta nie powiódł się, proszę spróbować ponownie",
                    ErrorCode.DB_ERROR
            );
        }
    }

    public User authenticateUser(String username, String passwordCandidate)
            throws DatabaseException, AuthenticationError, ValidationError {
        checkUsernameRequired(username);
        checkPasswordRequired(passwordCandidate);
        AuthUser user = getAuthUser(username);
        checkPasswordMatchesUser(user, passwordCandidate);
        return getUser(user.id);
    }
}

