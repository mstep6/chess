package dataaccess.sql;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class AuthDAOTests {
    AuthDAODatabase database = new AuthDAODatabase();

    public AuthDAOTests() throws SQLException, DataAccessException {
    }

    @BeforeEach
    void clearData() throws DataAccessException {
        database.clearAuthData();
    }

    @Test
    void generateAuthToken() {
        Assertions.assertNotNull(database.generateToken());
    }

    @Test
    void createAuthData() throws DataAccessException {
        database.createAuthData("myUsername", "authToken");
        AuthData result = database.findUser("authToken");
        Assertions.assertEquals("authToken", result.authToken());
        Assertions.assertEquals("myUsername", result.username());
    }

    @Test
    void createAuthDataInvalid() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> database.createAuthData(null, null));
    }

    @Test
    void createAuthDataInvalidUsername() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> database.createAuthData(null, "auth"));
    }

    @Test
    void createAuthDataInvalidAuthToken() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> database.createAuthData("username", null));
    }

    @Test
    void deleteAuthToken() throws DataAccessException {
        database.createAuthData("myUsername", "authToken");
        database.deleteAuthToken("authToken");
        Assertions.assertNull(database.findUser("authToken"));
    }

    @Test
    void clearAuthData() throws DataAccessException {
        database.createAuthData("user1", "auth1");
        database.createAuthData("user2", "auth2");
        database.createAuthData("user3", "auth3");
        database.clearAuthData();
        Assertions.assertNull(database.findUser("auth1"));
        Assertions.assertNull(database.findUser("auth2"));
        Assertions.assertNull(database.findUser("auth3"));
    }

    @Test
    void databaseNotClear() throws DataAccessException {
        database.createAuthData("user1", "auth1");
        database.createAuthData("user2", "auth2");
        database.clearAuthData();
        database.createAuthData("user3", "auth3");
        var result = database.findUser("auth3");
        Assertions.assertEquals(new AuthData("user3", "auth3"), result);
        Assertions.assertNull(database.findUser("auth1"));
    }

    @Test
    void findUser() throws DataAccessException {
        AuthData authdata = new AuthData("user1", "auth1");
        database.createAuthData("user1", "auth1");
        AuthData result = database.findUser("auth1");
        Assertions.assertEquals(authdata, result);
    }

    @Test
    void findUserThatDoesNotExist() throws DataAccessException {
        Assertions.assertNull(database.findUser("auth"));
    }

    @Test
    void authTokenExists() throws DataAccessException {
        database.createAuthData("user1", "auth1");
        Assertions.assertTrue(database.authTokenExists("auth1"));
    }

    @Test
    void authTokenDoesNotExist() throws DataAccessException {
        Assertions.assertFalse(database.authTokenExists("auth1"));
    }

}
