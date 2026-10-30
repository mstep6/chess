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


}
