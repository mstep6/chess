package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.getConnection;

public class UserDAODatabase implements UserDAO {
    public UserDAODatabase() throws SQLException, DataAccessException {
        createDatabase();
    }
    public boolean findUser(String username) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "SELECT username FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw new DataAccessException("Error retrieving password from database");
            }
            System.out.println(e.getMessage());
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
            if (e instanceof SQLException) {
                throw new DataAccessException("Not able to add duser to database");
            }
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
            }
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw new DataAccessException("Error retrieving password from database");
            }
            System.out.println(e.getMessage());
        }
        return "";
    }

    public void clearUsers() throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "DROP TABLE users";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e) {
            if (e instanceof SQLException) {
                throw new DataAccessException("Error clearing user table");
            }
            System.out.println(e.getMessage());
        }
    }



}
