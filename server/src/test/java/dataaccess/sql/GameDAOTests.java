package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resultsandrequests.JoinGameRequest;

import java.sql.SQLException;
import java.util.ArrayList;

public class GameDAOTests {
    GameDAO database = new GameDAODatabase();

    public GameDAOTests() throws SQLException, DataAccessException {
    }

    @BeforeEach
    void clearGames() throws DataAccessException {
        database.clearGameData();
    }

    @Test
    void createGame() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "gameName", new ChessGame());
        database.createGame(gameData);
        GameData game = database.findGame(gameData.gameID());
        Assertions.assertEquals(gameData, game);
    }

    @Test
    void createInvalidGame() {
        GameData gameData = new GameData(0, null, null, null, new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> database.createGame(gameData));
    }

    @Test
    void listGames() throws DataAccessException {
        GameData gameData1 = new GameData(1, null, null, "gameName1", new ChessGame());
        GameData gameData2 = new GameData(2, null, null, "gameName2", new ChessGame());
        GameData gameData3 = new GameData(3, null, null, "gameName3", new ChessGame());
        database.createGame(gameData1);
        database.createGame(gameData2);
        database.createGame(gameData3);
        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(gameData1);
        expected.add(gameData2);
        expected.add(gameData3);
        ArrayList<GameData> result = database.getGames();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void updatePlayersWhiteUsername() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "game", new ChessGame());
        database.createGame(gameData);
        JoinGameRequest joinGameRequest = new JoinGameRequest("auth", ChessGame.TeamColor.WHITE, 1);
        database.updatePlayers("username", joinGameRequest, gameData);
        GameData result = database.findGame(1);
        Assertions.assertEquals("username", result.whiteUsername());
    }

    @Test
    void updatePlayersBlackUsername() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "game", new ChessGame());
        database.createGame(gameData);
        JoinGameRequest joinGameRequest = new JoinGameRequest("auth", ChessGame.TeamColor.BLACK, 1);
        database.updatePlayers("username", joinGameRequest, gameData);
        GameData result = database.findGame(1);
        Assertions.assertEquals("username", result.blackUsername());
    }

    @Test
    void clearGamesTable() throws DataAccessException {
        database.createGame(new GameData(1, "white", "black", "game1", new ChessGame()));
        database.createGame(new GameData(2, "white2", "black2", "game2", new ChessGame()));
        database.createGame(new GameData(3, "white3", "black3", "game3", new ChessGame()));
        database.clearGameData();
        GameData result = database.findGame(1);
        Assertions.assertNull(result);
    }

    @Test
    void clearGamesAndAdd() throws DataAccessException {
        database.createGame(new GameData(1, "white", "black", "game1", new ChessGame()));
        database.createGame(new GameData(2, "white2", "black2", "game2", new ChessGame()));
        database.clearGameData();
        GameData result = database.findGame(1);
        Assertions.assertNull(result);
        GameData gameData = new GameData(1, "white", "black", "game", new ChessGame());
        database.createGame(gameData);
        var result2 = database.findGame(1);
        Assertions.assertEquals(gameData, result2);
    }
}
