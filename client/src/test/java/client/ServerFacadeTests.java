package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import resultsandrequests.*;
import server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(3000);
        System.out.println("Started test HTTP server on " + port);
        String portString = String.valueOf(port);
        System.out.println(portString);
        facade = new ServerFacade("http://localhost:" + portString);
    }
    @BeforeAll
    public static void clearDb() throws IOException, InterruptedException {
        facade.clearDb();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void register() throws IOException, InterruptedException {
        var result = facade.register(new RegisterRequest("user", "pass", "email"));
        Assertions.assertEquals("user", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void registerInvalidUsername() throws IOException, InterruptedException {
        var result = facade.register(new RegisterRequest(null, "pass", "email"));
        Assertions.assertNull(result);
    }

    @Test
    public void registerInvalidPassword() throws IOException, InterruptedException {
        var result = facade.register(new RegisterRequest("user", null, "email"));
        Assertions.assertNull(result);
    }

    @Test
    public void registerInvalidEmail() throws IOException, InterruptedException {
        var result = facade.register(new RegisterRequest("user", "pass", null));
        Assertions.assertNull(result);
    }

    @Test
    public void login() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("user", "pass", "email"));
        facade.logout(new LogoutRequest(registerResult.authToken()));
        var result = facade.login(new LoginRequest("user", "pass"));
        Assertions.assertEquals("user", result.username());
        Assertions.assertNotNull(result.authToken());
    }
    @Test
    public void loginWrongUsername() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("user", "pass", "email"));
        facade.logout(new LogoutRequest(registerResult.authToken()));
        var result = facade.login(new LoginRequest("usser", "pass"));
        Assertions.assertNull(result);
    }

    @Test
    public void loginWrongPassword() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("user", "pass", "email"));
        facade.logout(new LogoutRequest(registerResult.authToken()));
        var result = facade.login(new LoginRequest("user", "passs"));
        Assertions.assertNull(result);
    }

    @Test
    public void logout() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("coolUser", "coolPassword", "coolEmail"));
        var logoutResult = facade.logout(new LogoutRequest(registerResult.authToken()));
        LogoutResult expected = new LogoutResult();
        Assertions.assertEquals(expected, logoutResult);
    }

    @Test
    public void logoutNotLoggedIn() throws IOException, InterruptedException {
        var logoutResult = facade.logout(new LogoutRequest(null));
        Assertions.assertNull(logoutResult);
    }

//    @Test
//    public void logoutInvalidAuth() throws IOException, InterruptedException {
//        var registerResult = facade.register(new RegisterRequest("coolUser", "coolPassword", "coolEmail"));
//        var logoutResult = facade.logout(new LogoutRequest(null));
//        Assertions.assertNull(logoutResult);
//    }

    @Test
    public void listGames() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("coolUser", "coolPassword", "coolEmail"));
        var createGame1 = facade.createGame(new CreateGameRequest(registerResult.authToken(), "game1"));
        GameData game1Data = new GameData(1, null, null, "game1", new ChessGame());
        var createGame2 = facade.createGame(new CreateGameRequest(registerResult.authToken(), "game2"));
        GameData game2Data = new GameData(2, null, null, "game2", new ChessGame());
        var createGame3 = facade.createGame(new CreateGameRequest(registerResult.authToken(), "game3"));
        GameData game3Data = new GameData(3, null, null, "game3", new ChessGame());
        ArrayList<GameData> games = new ArrayList<>(Arrays.asList(game1Data, game2Data, game3Data));
        ListGamesResult expected = new ListGamesResult(games);
        var listGamesResult = facade.listGames(new ListGamesRequest(registerResult.authToken()));
        Assertions.assertEquals(expected, listGamesResult);
    }

    @Test
    public void listGamesWithoutLoggingIn() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("coolUser", "coolPassword", "coolEmail"));
        var createGame1 = facade.createGame(new CreateGameRequest(registerResult.authToken(), "game1"));
        var createGame2 = facade.createGame(new CreateGameRequest(registerResult.authToken(), "game2"));
        var createGame3 = facade.createGame(new CreateGameRequest(registerResult.authToken(), "game3"));
        facade.logout(new LogoutRequest(registerResult.authToken()));
        var listGamesResult = facade.listGames(new ListGamesRequest(registerResult.authToken()));
        Assertions.assertNull(listGamesResult);
    }

    @Test
    public void createGame() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("harryPotter", "gryffindor", "theChosenOne"));
        var createGame = facade.createGame(new CreateGameRequest(registerResult.authToken(), "wizardsChess"));
        CreateGameResult expected = new CreateGameResult(1);
        Assertions.assertEquals(expected, createGame);
    }

    @Test
    public void createGameWithoutName() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("harryPotter", "gryffindor", "theChosenOne"));
        var createGame = facade.createGame(new CreateGameRequest(registerResult.authToken(), null));
        Assertions.assertNull(createGame);
    }

    @Test
    public void createGameWithoutLoggingIn() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("harryPotter", "gryffindor", "theChosenOne"));
        facade.logout(new LogoutRequest(registerResult.authToken()));
        var createGame = facade.createGame(new CreateGameRequest(registerResult.authToken(), "wizardsChess"));
        Assertions.assertNull(createGame);
    }

    @Test
    public void joinGame() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("harryPotter", "gryffindor", "theChosenOne"));
        var createGame = facade.createGame(new CreateGameRequest(registerResult.authToken(), "wizardsChess"));
        var joinGameResult = facade.joinGame(new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, 1));
        JoinGameResult expected = new JoinGameResult();
        Assertions.assertEquals(expected, joinGameResult);
    }

    @Test
    public void joinGameThatDoesNotExist() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("harryPotter", "gryffindor", "theChosenOne"));
        var joinGameResult = facade.joinGame(new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, 1));
        Assertions.assertNull(joinGameResult);
    }

    @Test
    public void joinGameWithoutLoggingIn() throws IOException, InterruptedException {
        var registerResult = facade.register(new RegisterRequest("harryPotter", "gryffindor", "theChosenOne"));
        var createGame = facade.createGame(new CreateGameRequest(registerResult.authToken(), "wizardsChess"));
        facade.logout(new LogoutRequest(registerResult.authToken()));
        var joinGameResult = facade.joinGame(new JoinGameRequest(null, ChessGame.TeamColor.WHITE, 1));
        Assertions.assertNull(joinGameResult);
    }


}
