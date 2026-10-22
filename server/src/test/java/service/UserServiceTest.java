package service;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import exceptions.WrongPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resultsandrequests.*;

class UserServiceTest {
    UserService userService = new UserService();

    @BeforeEach
    void clearData() {
        userService.clearDatabase();
    }

    @Test
    void register() throws AlreadyTakenException, BadRequestException {
        String username = "myUsername";
        String password = "myPassword";
        String email = "myEmail@gmail.com";
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.register(request);
        Assertions.assertEquals(username, result.username());
        Assertions.assertNotNull(result.authToken(), "Auth token was null");
    }

    @Test
    void registerBadRequest() {
        RegisterRequest request = new RegisterRequest(null, "password", "email");
        Assertions.assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    void registerAlreadyTaken() throws BadRequestException, AlreadyTakenException {
        userService.register(new RegisterRequest("myUsername", "1234", "email@gmail.com"));
        RegisterRequest newRequest = new RegisterRequest("myUsername", "5678", "m@gmail.com");
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(newRequest));
    }

    @Test
    void checkPassword() throws BadRequestException, AlreadyTakenException {
        userService.register(new RegisterRequest("username", "password", "email@byu.edu"));
        Assertions.assertTrue(userService.checkPassword("username", "password"));
    }

    @Test
    void wrongPassword() throws BadRequestException, AlreadyTakenException {
        userService.register(new RegisterRequest("username", "password", "email@byu.edu"));
        Assertions.assertFalse(userService.checkPassword("username", "passwrd"));
    }

    @Test
    void login() throws UnauthorizedException, WrongPasswordException, BadRequestException, AlreadyTakenException {
        String username = "test123";
        String password = "passwordTest456";
        userService.register(new RegisterRequest(username, password, "email"));
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult loginResult = userService.login(loginRequest);
        Assertions.assertEquals(username, loginResult.username());
        Assertions.assertNotNull(loginResult.authToken(), "Auth token was null");
    }

    @Test
    void userDoesNotExistLogin() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }


    @Test
    void logout() throws BadRequestException, AlreadyTakenException, UnauthorizedException, WrongPasswordException {
        RegisterResult registerResult = userService.register(new RegisterRequest("usrnme", "pswrd", "email"));
        LoginResult loginResult = userService.login(new LoginRequest("usrnme", "pswrd"));
        LogoutResult logoutResult = userService.logout(new LogoutRequest(loginResult.authToken()));
        LogoutResult expectedResult = new LogoutResult();
        Assertions.assertEquals(expectedResult, logoutResult);
    }

    @Test
    void logoutInvalidAuthToken() throws BadRequestException, AlreadyTakenException, UnauthorizedException, WrongPasswordException {
        RegisterResult registerResult = userService.register(new RegisterRequest("usrnme", "pswrd", "email"));
        LoginResult loginResult = userService.login(new LoginRequest("usrnme", "pswrd"));
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout(new LogoutRequest("abc")));
    }
}