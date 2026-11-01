package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class UserDAODatabase implements UserDAO {
    public UserDAODatabase() throws SQLException, DataAccessException {
        try {
            createDatabase();
        }
        catch(Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error starting database");
            }
        }
    }

    public boolean findUser(String username) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "SELECT username FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var result = preparedStatement.executeQuery();
                while (result.next()) {
                    if (result.getString("username").equals(username)) {
                        return true;
                    }
                }
            }
            catch (Exception e) {
                if (e instanceof SQLException) {
                    throw new DataAccessException("Error finding user");
                }
                return false;
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Error finding user");
        }
        return false;
    }
    public void createUser(UserData userData) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                var hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Not able to add user to the database");
        }
    }

    public String getPassword(String username) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "SELECT password FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var password = preparedStatement.executeQuery();
                while (password.next()) {
                    return password.getString("password");
                }
                return null;
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Error retrieving password from database");
            return null;
        }
    }

    public void clearUsers() throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "TRUNCATE TABLE users";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e) {
            handleSQLDataExceptions(e, "Error clearing user data");
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var userData = preparedStatement.executeQuery();
                while (userData.next()) {
                    String user = userData.getString("username");
                    String password = userData.getString("password");
                    String email = userData.getString("email");
                    return new UserData(user, password, email);
                }
                return null;
            }
        }
        catch (Exception e) {
            handleSQLDataExceptions(e, "Error retrieving user data");
            return null;
            }
        }
}
