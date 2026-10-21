package dataaccess;

import model.GameData;
import resultsandrequests.JoinGameRequest;

import java.util.ArrayList;
import java.util.Map;

public interface GameDAO {
    void clearGameData();
    void createGame(GameData gameData);
    GameData findGame(Integer gameID);
    ArrayList<Map<Integer, GameData>> getGames();
    void updatePlayers(String username, JoinGameRequest joinGameRequest, GameData gameData);
}
