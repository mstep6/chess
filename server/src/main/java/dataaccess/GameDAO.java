package dataaccess;

import model.GameData;
import resultsandrequests.JoinGameRequest;
import java.util.ArrayList;

public interface GameDAO {
    void clearGameData();
    void createGame(GameData gameData);
    GameData findGame(Integer gameID);
    ArrayList<GameData> getGames();
    void updatePlayers(String username, JoinGameRequest joinGameRequest, GameData gameData);
}
