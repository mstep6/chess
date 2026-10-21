package service;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resultsandrequests.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    GameService gameService = new GameService();
    UserService userService = new UserService();


    @BeforeEach
    void clearData() {
        userService.clearDatabase();
    }

    @Test
    void createGame() throws BadRequestException, UnauthorizedException {
        CreateGameRequest game = new CreateGameRequest("abc", "game1");
        CreateGameResult gameResult = gameService.createGame(game);
        Assertions.assertEquals(1, gameResult.gameID());
    }

    @Test
    void createGameNullAuthToken() throws BadRequestException, UnauthorizedException {
        CreateGameRequest game = new CreateGameRequest(null, "game1");
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.createGame(game));
    }

    @Test
    void createGameNullGameName() throws BadRequestException, UnauthorizedException {
        CreateGameRequest game = new CreateGameRequest("abc", null);
        Assertions.assertThrows(BadRequestException.class, () -> gameService.createGame(game));
    }

    @Test
    void checkPlayerColor() {
    }

    @Test
    void joinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, GameDoesNotExistException {
        RegisterResult registerResult = userService.register(new RegisterRequest("user", "monkey", "email"));
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        JoinGameResult joinGameResult = gameService.joinGame(new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, createGameResult.gameID()));
        JoinGameResult expectedResult = new JoinGameResult();
        Assertions.assertEquals(expectedResult, joinGameResult);

        ListGamesResult listGamesResult = gameService.listAllGames(new ListGamesRequest(registerResult.authToken()));
        ArrayList<GameData> expectedList = new ArrayList<>();
        expectedList.add(new GameData(1, "user", null, createGameRequest.gameName(), new ChessGame()));
        ListGamesResult expectedListResult = new ListGamesResult(expectedList);
        Assertions.assertEquals(expectedListResult, listGamesResult);
    }

    @Test
    void listAllGames() throws BadRequestException, UnauthorizedException, WrongPasswordException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(new RegisterRequest("usr", "pswrd", "email"));
        CreateGameRequest game1 = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameRequest game2 = new CreateGameRequest(registerResult.authToken(), "game2");
        gameService.createGame(game1);
        gameService.createGame(game2);
        ListGamesResult listGamesResult = gameService.listAllGames(new ListGamesRequest(registerResult.authToken()));
        ArrayList<GameData> expectedList = new ArrayList<>();
        expectedList.add( new GameData(1, null, null, "game1", new ChessGame()));
        expectedList.add(new GameData(2, null, null, "game2", new ChessGame()));
        ListGamesResult expectedResult = new ListGamesResult(expectedList);
        Assertions.assertEquals(expectedResult, listGamesResult);
        Assertions.assertEquals(2, listGamesResult.games().size());
    }
}