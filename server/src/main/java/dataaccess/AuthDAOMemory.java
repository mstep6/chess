package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDAOMemory implements AuthDAO{
    private final static HashMap<String, AuthData> authInfo = new HashMap<>();

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuthData(String username, String authToken) {
        AuthData authData = new AuthData(username, authToken);
        authInfo.put(authToken, authData);
        return authData;
    }

    public void deleteAuthToken(String authToken) {
        authInfo.remove(authToken);
    }

    public void clearAuthData() {
        authInfo.clear();
    }


    public AuthData findUser(String authToken) {
        return authInfo.get(authToken);
    }

    public boolean authTokenExists(String authToken) {
        if (authInfo.containsKey(authToken)) {
            return true;
        }
        return false;
    }
}
