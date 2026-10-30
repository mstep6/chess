package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String generateToken();
    AuthData createAuthData(String username, String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
    void clearAuthData() throws DataAccessException;
    AuthData findUser(String authToken) throws DataAccessException;
    boolean authTokenExists(String authToken) throws DataAccessException;
}
