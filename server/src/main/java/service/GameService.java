package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.GameDoesNotExistException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import resultsandrequests.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class GameService {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private int gameID = 0;

    public GameService() throws DataAccessException {
        try {
            userDAO = new UserDAODatabase();
            authDAO = new AuthDAODatabase();
            gameDAO = new GameDAODatabase();
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw new DataAccessException("Error starting the game service");
            }
            else {
                System.out.println(e.getMessage());
                throw new RuntimeException();
            }

        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (!authDAO.authTokenExists(createGameRequest.authToken())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        else {
            gameID += 1;
            String gameName = createGameRequest.gameName();
            ChessGame game = new ChessGame();
            GameData gameData = new GameData(gameID, null, null, gameName, game);
            gameDAO.createGame(gameData);
            return new CreateGameResult(gameID);
        }
    }

    public boolean checkPlayerColor(JoinGameRequest joinGameRequest, GameData gameData) {
        boolean canJoinGame = false;
        ChessGame.TeamColor requestedPlayerColor = joinGameRequest.playerColor();
        if (requestedPlayerColor.equals(ChessGame.TeamColor.WHITE)) {
            if (gameData.whiteUsername() == null) {
                canJoinGame = true;
            }
        }
        else if (requestedPlayerColor.equals(ChessGame.TeamColor.BLACK)) {
            if (gameData.blackUsername() == null) {
                canJoinGame = true;
            }
        }
        return canJoinGame;
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest)
            throws BadRequestException, GameDoesNotExistException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        if (joinGameRequest.gameID() == null || joinGameRequest.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (!authDAO.authTokenExists(joinGameRequest.authToken())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        else {
            GameData gameData = gameDAO.findGame(joinGameRequest.gameID());
            if (gameData == null) {
                throw new GameDoesNotExistException("Error: bad request");
            }
            else {
                if (checkPlayerColor(joinGameRequest, gameData)) {
                    AuthData userAuthInfo = authDAO.findUser(joinGameRequest.authToken());
                    gameDAO.updatePlayers(userAuthInfo.username(), joinGameRequest, gameData);
                    return new JoinGameResult();
                }
                else{
                    throw new AlreadyTakenException("Error: already taken");
                }
            }
        }
    }

    public ListGamesResult listAllGames(ListGamesRequest listGamesRequest) throws UnauthorizedException, DataAccessException {
        if (!authDAO.authTokenExists(listGamesRequest.authToken())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        ArrayList<GameData> gameList = gameDAO.getGames();
        return new ListGamesResult(gameList);
    }



}
