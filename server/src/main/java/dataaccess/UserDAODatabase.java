package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDAODatabase implements UserDAO {
    public UserDAODatabase() throws SQLException, DataAccessException {
        configureDatabase();
    }
    boolean findUser(String username) {

    }
    void createUser(UserData userData) {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

    }
    String getPassword(String username) {

    }
    void clearUsers() {
        var statement = "DROP TABLE users";
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            username varchar(256) NOT NULL,
            password varchar(256) NOT NULL,
            email varchar(256) NOT NULL,
            PRIMARY KEY (username),
            INDEX(password)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
"""
    };

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
