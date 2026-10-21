package service;

import dataaccess.*;
import model.UserData;
import resultsandrequests.*;

public class UserService {
    private final UserDAO userDAO = new UserDAOMemory();
    private final AuthDAO authDAO = new AuthDAOMemory();
    private final GameDAO gameDAO = new GameDAOMemory();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException {
        String username = registerRequest.username();
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("Error: unauthorized");
        }
        boolean userExists = userDAO.findUser(username);
        if (userExists) {
            throw new AlreadyTakenException("Error: username already taken");
        }
        else {
            UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(userData);
            String authToken = authDAO.generateToken();
            var authData = authDAO.createAuthData(registerRequest.username(), authToken);
            return new RegisterResult(authData.username(), authData.authToken());
        }
    }

    public boolean checkPassword(String username, String password) {
        if (password.equals(userDAO.getPassword(username))) {
            return true;
        }
        return false;
    }

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, WrongPasswordException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        boolean userExists = userDAO.findUser(username);
        if (!userExists) {
            throw new UnauthorizedException("error: unauthorized");
        }
        else {
            if (!checkPassword(username, password)) {
                throw new WrongPasswordException("error: incorrect password");
            }
            else {
                String authToken = authDAO.generateToken();
                var authData = authDAO.createAuthData(loginRequest.username(), authToken);
                return new LoginResult(authData.username(), authData.authToken());
            }
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws UnauthorizedException {
        String authToken = logoutRequest.authToken();
        if (!authDAO.authTokenExists(authToken)) {
            throw new UnauthorizedException("error: unauthorized");
        }
        authDAO.deleteAuthToken(authToken);
        return new LogoutResult();
    }

    public void clearDatabase() {
        userDAO.clearUsers();
        authDAO.clearAuthData();
        gameDAO.clearGameData();
    }

}
