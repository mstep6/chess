package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    boolean findUser(String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    String getPassword(String username) throws DataAccessException;
    void clearUsers() throws DataAccessException;
}
