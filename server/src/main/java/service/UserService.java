package service;

import dataaccess.*;
import dataaccess.UserDAODatabase;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import exceptions.WrongPasswordException;
import model.UserData;
import resultsandrequests.*;

import java.sql.SQLException;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private final GameDAO gameDAO = new GameDAOMemory();

//    public UserService() throws SQLException, DataAccessException {
//    }

    public UserService () throws SQLException, DataAccessException {
        try {
            userDAO = new UserDAODatabase();
            authDAO = new AuthDAODatabase();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException, DataAccessException {
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

    public boolean checkPassword(String username, String password) throws DataAccessException {
        if (password.equals(userDAO.getPassword(username))) {
            return true;
        }
        return false;
    }

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, WrongPasswordException, DataAccessException {
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

    public LogoutResult logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException {
        String authToken = logoutRequest.authToken();
        if (!authDAO.authTokenExists(authToken)) {
            throw new UnauthorizedException("error: unauthorized");
        }
        authDAO.deleteAuthToken(authToken);
        return new LogoutResult();
    }

    public void clearDatabase() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuthData();
        gameDAO.clearGameData();
    }

}
