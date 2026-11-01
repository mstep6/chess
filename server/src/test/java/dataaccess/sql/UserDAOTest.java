package dataaccess.sql;

import dataaccess.DataAccessException;
import exceptions.BadRequestException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import resultsandrequests.RegisterRequest;
import service.UserService;

import java.sql.SQLException;

class UserDAOTest {

    UserDAODatabase database = new UserDAODatabase();
    UserService userService = new UserService();

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
    void createUserInvalidUsername() {
        UserData userData = new UserData(null, "password", "email@gmail.com");
        Assertions.assertThrows(DataAccessException.class, () -> database.createUser(userData));
    }

    @Test
    void createUserInvalidPassword() {
        resultsandrequests.RegisterRequest registerRequest = new RegisterRequest("user", null, "email@gmail.com");
        Assertions.assertThrows(BadRequestException.class, () -> userService.register(registerRequest));
    }

    @Test
    void createUserInvalidEmail() {
        resultsandrequests.RegisterRequest registerRequest = new RegisterRequest("user", "password", null);
        Assertions.assertThrows(BadRequestException.class, () -> userService.register(registerRequest));
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
    void findUserThatDoesNotExistError() throws DataAccessException {
        Assertions.assertFalse(database.findUser("myUsername"));
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
    void clearUserTableAndAdd() throws DataAccessException {
        database.createUser(new UserData("user1", "pass1", "email1"));
        database.createUser(new UserData("user2", "pass2", "email2"));
        database.clearUsers();
        database.createUser(new UserData("user3", "pass3", "email3"));
        boolean foundInvalid = database.findUser("user1");
        Assertions.assertFalse(foundInvalid);
        boolean found = database.findUser("user3");
        Assertions.assertTrue(found);
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

    @Test
    void getUser() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email");
        database.createUser(userData);
        var result = database.findUser("username");
        Assertions.assertTrue(result);
    }

    @Test
    void getUserThatDoesNotExist() throws DataAccessException {
        var result = database.findUser("username");
        Assertions.assertFalse(result);
    }

}