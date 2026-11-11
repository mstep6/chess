package dataaccess;

import model.GameData;
import resultsandrequests.JoinGameRequest;
import java.util.ArrayList;

public interface GameDAO {
    void clearGameData() throws DataAccessException;
    int createGame(GameData gameData) throws DataAccessException;
    GameData findGame(Integer gameID) throws DataAccessException;
    ArrayList<GameData> getGames() throws DataAccessException;
    void updatePlayers(String username, JoinGameRequest joinGameRequest, GameData gameData) throws DataAccessException;
}
