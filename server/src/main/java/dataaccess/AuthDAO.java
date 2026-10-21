package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String generateToken();
    AuthData createAuthData(String username, String authToken);
    void deleteAuthToken(String authToken);
    void clearAuthData();
    AuthData findUser(String authToken);
    boolean authTokenExists(String authToken);
}
