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
    private final static ArrayList<GameData> gameInfo = new ArrayList<>();

    public void createGame(GameData gameData) {
        int gameID = gameData.gameID();
        gameInfo.add(gameData);
    }
    public void clearGameData() {
        gameInfo.clear();
    }

    public GameData findGame(Integer gameID) {
        for (var game : gameInfo) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public ArrayList<GameData> getGames() {
        return gameInfo;
    }

    public void updatePlayers(String username, JoinGameRequest joinGameRequest, GameData gameData) {
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame chessGame = gameData.game();

        int index = 0;
        for (var game : gameInfo) {
            if (game == gameData) {
                if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.WHITE)) {
                    gameInfo.set(index, new GameData(gameID, username, blackUsername, gameName, chessGame));
                }
                else if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.BLACK)) {
                    gameInfo.set(index, new GameData(gameID, whiteUsername, username, gameName, chessGame));
                }
            }
            index += 1;
        }
    }
}
