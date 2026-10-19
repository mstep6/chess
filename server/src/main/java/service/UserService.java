package service;

import dataaccess.AuthDAO;
import dataaccess.AuthDAOMemory;
import dataaccess.UserDAO;
import dataaccess.UserDAOMemory;
import model.UserData;
import resultsandrequests.*;

public class UserService {
    private final UserDAO userDAO = new UserDAOMemory();
    private final AuthDAO authDAO = new AuthDAOMemory();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException {
        String username = registerRequest.username();
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
//    public void logout(LogoutRequest logoutRequest) {}
//}
}
