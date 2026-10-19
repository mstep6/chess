package dataaccess;

import model.UserData;

public interface UserDAO {
    boolean findUser(String username);
    void createUser(UserData userData);
    String getPassword(String username);
}
