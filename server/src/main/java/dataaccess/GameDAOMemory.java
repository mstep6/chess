package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDAOMemory implements GameDAO {
    private final static ArrayList<Map<Integer, GameData>> gameInfo = new ArrayList<>();

    public void createGame(GameData gameData) {
        int gameID = gameData.gameID();
        gameInfo.add(Map.of(gameID, gameData));
    }
    public void clearGameData() {
        gameInfo.clear();
    }
}
