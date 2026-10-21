package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOMemoryTest {
    private HashMap<String, UserData> userInfo;

    @BeforeEach
    void setup() {
        userInfo = new HashMap<>();
    }

    void findUser() {

    }


}