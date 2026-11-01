package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import resultsandrequests.JoinGameRequest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.getConnection;

public class GameDAODatabase implements GameDAO {

    public GameDAODatabase() throws SQLException, DataAccessException {
        try {
            createDatabase();
        }
        catch(Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error starting database");
            }
        }
    }

    public void clearGameData() throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "TRUNCATE TABLE games";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error clearing game data");
            }
            System.out.println(e.getMessage());
        }
    }

    public void createGame(GameData gameData) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4, gameData.gameName());
                var jsonGame = new Gson().toJson(gameData.game());
                preparedStatement.setString(5, jsonGame);
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error creating game");
            }
            System.out.println(e.getMessage());
        }
    }

    public GameData findGame(Integer gameID) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                var result = preparedStatement.executeQuery();
                while (result.next()) {
                    int gameIDnum = result.getInt("gameID");
                    String whiteUsername = result.getString("whiteUsername");
                    String blackUsername = result.getString("blackUsername");
                    String gameName = result.getString("gameName");
                    String gameJson = result.getString("game");
                    ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                    return (new GameData(gameIDnum, whiteUsername, blackUsername, gameName, game));
                }
                return null;
            }
        }
        catch (Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error finding game");
            }
            else {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

    public ArrayList<GameData> getGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = getConnection()) {
            var statement = "SELECT * FROM games";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var result = preparedStatement.executeQuery();
                while (result.next()) {
                    GameData gameInfo = findGame(result.getInt("gameID"));
                    games.add(gameInfo);
                }
                return games;
            }
        }
        catch (Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error retrieving game list");
            }
            else {
                System.out.println(e.getMessage());
                return null;
            }

        }
    }

    public void updatePlayers(String username, JoinGameRequest joinGameRequest, GameData gameData) throws DataAccessException {
        var teamColor = joinGameRequest.playerColor();
        var statement = "";
        if (teamColor == ChessGame.TeamColor.WHITE) {
            statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
        }
        else {
            statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
        }

        try (Connection conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameData.gameID());
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e) {
            if (e instanceof SQLException || e instanceof DataAccessException) {
                throw new DataAccessException("Error occurred while player tried to join game");
            }
            else {
                System.out.println(e.getMessage());
            }
        }
    }
}
