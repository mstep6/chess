package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDAOMemory implements UserDAO{
    private final HashMap<String, UserData> userInfo = new HashMap<>();

    public boolean findUser(String username) {
        if (userInfo.get(username) != null) {
            return true;
        }
        return false;
    }

    public void createUser(UserData userData) {
        userInfo.put(userData.username(), userData);
    }
}
