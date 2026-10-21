package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import resultsandrequests.*;

import java.util.ArrayList;
import java.util.Map;

import static chess.ChessGame.TeamColor.WHITE;

public class GameService {
    private final UserDAO userDAO = new UserDAOMemory();
    private final AuthDAO authDAO = new AuthDAOMemory();
    private final GameDAO gameDAO = new GameDAOMemory();
    private int gameID = 0;

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws BadRequestException, UnauthorizedException {
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

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws BadRequestException, GameDoesNotExistException, AlreadyTakenException, UnauthorizedException {
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

    public ListGamesResult listAllGames(ListGamesRequest listGamesRequest) throws UnauthorizedException {
        if (!authDAO.authTokenExists(listGamesRequest.authToken())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        ArrayList<Map<Integer, GameData>> gameList = gameDAO.getGames();
        return new ListGamesResult(gameList);
    }

}
