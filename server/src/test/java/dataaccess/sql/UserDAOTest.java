package dataaccess.sql;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

class UserDAOTest {

    UserDAODatabase database = new UserDAODatabase();

    UserDAOTest() throws SQLException, DataAccessException {
    }

    @BeforeEach
    void clearData() throws DataAccessException {
        database.clearUsers();
    }

    @Test
    void createUser() throws DataAccessException {
        UserData userData = new UserData("myUsername", "password", "email@gmail.com");
        database.createUser(userData);
        UserData result = database.getUser("myUsername");
        Assertions.assertEquals(userData.username(), result.username());
        Assertions.assertTrue(BCrypt.checkpw(userData.password(), result.password()));
        Assertions.assertEquals(userData.email(), result.email());
    }

    @Test
    void findUser() throws DataAccessException {
        UserData userData = new UserData("myUsername", "password", "email@gmail.com");
        database.createUser(userData);
        boolean found = database.findUser("myUsername");
        Assertions.assertTrue(found);
    }

    @Test
    void findUserThatDoesNotExist() throws DataAccessException {
        boolean found = database.findUser("myUsername");
        Assertions.assertFalse(found);
    }

    @Test
    void clearUserTable() throws DataAccessException {
        database.createUser(new UserData("user1", "pass1", "email1"));
        database.createUser(new UserData("user2", "pass2", "email2"));
        database.createUser(new UserData("user3", "pass3", "email3"));
        database.clearUsers();
        boolean found = database.findUser("user1");
        Assertions.assertFalse(found);
    }

    @Test
    void getPassword() throws DataAccessException {
        UserData userData = new UserData("myUsername", "password", "email@gmail.com");
        database.createUser(userData);
        String password = database.getPassword("myUsername");
        Assertions.assertTrue(BCrypt.checkpw("password", password));
    }

    @Test
    void getPasswordThatDoesNotExist() throws DataAccessException {
        String password = database.getPassword("myUsername");
        Assertions.assertNull(password);
    }

}