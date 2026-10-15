package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String generateToken();
    AuthData createAuthData(String username, String authToken);
}
