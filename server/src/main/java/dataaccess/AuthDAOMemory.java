package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDAOMemory implements AuthDAO{
    private final static HashMap<String, AuthData> AUTH_INFO = new HashMap<>();

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuthData(String username, String authToken) {
        AuthData authData = new AuthData(username, authToken);
        AUTH_INFO.put(authToken, authData);
        return authData;
    }

    public void deleteAuthToken(String authToken) {
        AUTH_INFO.remove(authToken);
    }

    public void clearAuthData() {
        AUTH_INFO.clear();
    }


    public AuthData findUser(String authToken) {
        return AUTH_INFO.get(authToken);
    }

    public boolean authTokenExists(String authToken) {
        if (AUTH_INFO.containsKey(authToken)) {
            return true;
        }
        return false;
    }
}
