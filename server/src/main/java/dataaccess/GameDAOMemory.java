package dataaccess;

import chess.ChessGame;
import model.GameData;
import resultsandrequests.JoinGameRequest;
import java.util.ArrayList;


public class GameDAOMemory implements GameDAO {
    private final static ArrayList<GameData> GAME_INFO = new ArrayList<>();

    public void createGame(GameData gameData) {
        int gameID = gameData.gameID();
        GAME_INFO.add(gameData);
    }
    public void clearGameData() {
        GAME_INFO.clear();
    }

    public GameData findGame(Integer gameID) {
        for (var game : GAME_INFO) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public ArrayList<GameData> getGames() {
        return GAME_INFO;
    }

    public void updatePlayers(String username, JoinGameRequest joinGameRequest, GameData gameData) {
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame chessGame = gameData.game();

        int index = 0;
        for (var game : GAME_INFO) {
            if (game == gameData) {
                if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.WHITE)) {
                    GAME_INFO.set(index, new GameData(gameID, username, blackUsername, gameName, chessGame));
                }
                else if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.BLACK)) {
                    GAME_INFO.set(index, new GameData(gameID, whiteUsername, username, gameName, chessGame));
                }
            }
            index += 1;
        }
    }
}
