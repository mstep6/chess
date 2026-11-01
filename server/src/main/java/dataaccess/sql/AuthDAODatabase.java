package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static dataaccess.DatabaseManager.*;

public class AuthDAODatabase implements AuthDAO {

    public AuthDAODatabase() throws SQLException, DataAccessException {
        try {
            createDatabase();
        }
        catch(Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error starting database");
            }
        }
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuthData(String username, String authToken) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "INSERT INTO auth (authtoken, username) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Error adding auth data");
        }
        return new AuthData(username, authToken);
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "DELETE FROM auth WHERE authtoken = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Error deleting auth data");
        }
    }

    public void clearAuthData() throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "TRUNCATE TABLE auth";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Error clearing auth data");
        }
    }

    public AuthData findUser(String authToken) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "SELECT authtoken, username FROM auth WHERE authtoken = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                var authData = preparedStatement.executeQuery();
                while (authData.next()) {
                    var userAuthToken = authData.getString("authtoken");
                    var username = authData.getString("username");
                    return new AuthData(username, userAuthToken);
                }
                return null;
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Error finding user given auth token");
            return null;
        }
    }

    public boolean authTokenExists(String authToken) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "SELECT authtoken FROM auth WHERE authtoken = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                var result = preparedStatement.executeQuery();
                while (result.next()) {
                    if (result.getString("authtoken").equals(authToken)) {
                        return true;
                    }
                }
            }
            catch (Exception e) {
                handleSQLDataExceptions(e, "Error finding the auth token");
                return false;
            }
        } catch (Exception e) {
            handleSQLDataExceptions(e, "Error Auth token could not be verified");
        }
        return false;
    }
}
