package service;

import dataaccess.AuthDAO;
import dataaccess.AuthDAOMemory;
import dataaccess.UserDAO;
import dataaccess.UserDAOMemory;
import model.UserData;
import resultsandrequests.AlreadyTakenException;
import resultsandrequests.RegisterRequest;
import resultsandrequests.RegisterResult;

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
//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
//}
}
