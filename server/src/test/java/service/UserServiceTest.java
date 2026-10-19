package service;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resultsandrequests.AlreadyTakenException;
import resultsandrequests.RegisterRequest;
import resultsandrequests.RegisterResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService service = new UserService();

    @Test
    void register() throws AlreadyTakenException {
        String username = "myUsername";
        String password = "myPassword";
        String email = "myEmail@gmail.com";
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = service.register(request);
        Assertions.assertEquals(username, result.username());
        Assertions.assertNotNull(result.authToken(), "Auth token was null");
    }

    @Test
    void checkPassword() {

    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}