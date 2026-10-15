package dataaccess;

import model.AuthData;

import java.util.UUID;

public class AuthDAOMemory implements AuthDAO{
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuthData(String username, String authToken) {
        AuthData authData = new AuthData(username, authToken);
        return authData;
    }
}
