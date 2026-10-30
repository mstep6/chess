package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class AuthDAOTests {
    AuthDAODatabase database = new AuthDAODatabase();

    @BeforeEach
    void clearData() throws DataAccessException {
        database.clearAuthData();
    }

    @Test
    void createAuthData() throws DataAccessException {
        database.createAuthData("myUsername", "authToken");
        AuthData result = database.findUser("authToken");
        Assertions.assertEquals("authToken", result.authToken());
        Assertions.assertEquals("myUsername", result.username());
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
