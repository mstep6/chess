package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import model.UserData;
import resultsandrequests.GameDoesNotExistException;
import resultsandrequests.JoinGameRequest;

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

    public GameData findGame(Integer gameID) {
        for (var game : gameInfo) {
            if (game.containsKey(gameID)) {
                return game.get(gameID);
            }
        }
        return null;
    }

    public ArrayList<Map<Integer, GameData>> getGames() {
        return gameInfo;
    }

    public void updatePlayers(String username, JoinGameRequest joinGameRequest, GameData gameData) {
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();

        int index = 0;
        for (var gameMap : gameInfo) {
            if (gameMap.containsKey(gameID)) {
                if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.WHITE)) {
                    gameInfo.set(index, Map.of(gameID, new GameData(gameID, username, blackUsername, gameName, game)));
                }
                else if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.BLACK)) {
                    gameInfo.set(index, Map.of(gameID, new GameData(gameID, whiteUsername, username, gameName, game)));
                }
            }
            index += 1;
        }
    }
}
