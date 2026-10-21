package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDAOMemory implements UserDAO{
    private final static HashMap<String, UserData> USER_INFO = new HashMap<>();

    public boolean findUser(String username) {
        if (USER_INFO.get(username) != null) {
            return true;
        }
        return false;
    }

    public String getPassword(String username) {
        UserData userData = USER_INFO.get(username);
        return userData.password();
    }

    public void createUser(UserData userData) {
        USER_INFO.put(userData.username(), userData);
    }

    public void clearUsers() {
        USER_INFO.clear();
    }
}
