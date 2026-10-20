package dataaccess;

import model.GameData;

public interface GameDAO {
    void clearGameData();
    void createGame(GameData gameData);
}
