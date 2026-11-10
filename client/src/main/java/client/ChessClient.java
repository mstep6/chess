package client;

import chess.ChessGame;
import model.AuthData;
import resultsandrequests.*;
import ui.EscapeSequences;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;


public class ChessClient {
    private final ServerFacade server;
    private State userState = State.LOGGED_OUT;
    private AuthData authData;

    public ChessClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println("\uD83D\uDC51 Welcome to 240 Chess! Type 'help' to get started. \uD83D\uDC51");
        help();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            setupUserPrompt();
            result = scanner.nextLine();
            if (result.equals("quit")) {
                System.out.println("Thank you for playing!");
                break;
            }
            executeCommand(result);
        }
    }

    public void executeCommand(String input) {
        String command = "help";
        try {
            String[] tokens = input.split(" ");
            if (tokens.length > 0) {
                command = tokens[0].toLowerCase();
            }
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "help" -> help();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
//                case "observe" -> observeGame(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void help() {
        if (userState.equals(State.LOGGED_OUT)) {
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - create an account");
            System.out.println("login <USERNAME> <PASSWORD> - login to your account to play chess");
            System.out.println("quit - quit playing chess");
            System.out.println("help - list possible commands");
        }
        else {
            System.out.println("create <GAME NAME> - create a game");
            System.out.println("list - list all of the current games");
            System.out.println("join <ID> <WHITE | BLACK> - join a game");
            System.out.println("observe <ID> - observe a game");
            System.out.println("logout - logout of your account when you are finished");
            System.out.println("quit - quit playing chess");
            System.out.println("help - list possible commands");
        }
    }

    private void setupUserPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_MAGENTA);
    }

    private void register(String[] params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException("You need to input a username, password, and email to register.");
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        try {
             checkIfLoggedOut();
             RegisterResult registerResult = server.register(new RegisterRequest(username, password, email));
             if (registerResult == null) {
                 System.out.println("Registration unsuccessful");
             }
             else {
                 userState = State.LOGGED_IN;
                 authData = new AuthData(registerResult.username(), registerResult.authToken());
                 System.out.println("You have successfully registered!\n");
                 help();
             }

        } catch (Exception e) {
            handleErrors(e);
        }
    }

    private void login(String[] params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException("You need to input a username and password to login");
        }
        String username = params[0];
        String password = params[1];
        try {
            checkIfLoggedOut();
            LoginResult loginresult = server.login(new LoginRequest(username, password));
            if (loginresult == null) {
                System.out.println("Login unsuccessful");
            }
            else {
                userState = State.LOGGED_IN;
                authData = new AuthData(loginresult.username(), loginresult.authToken());
                System.out.println("You are logged in.\n");
                help();
            }
        } catch (Exception e) {
            handleErrors(e);
        }
    }

    private void createGame(String[] params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException("You need to input a game name to create a game");
        }
        String gameName = params[0];
        try {
            checkIfLoggedIn();
            CreateGameResult createGameResult = server.createGame(new CreateGameRequest(authData.authToken(), gameName));
            if (createGameResult == null) {
                System.out.println("Error occurred while creating game");
            }
            else {
                System.out.println("This game was created\n");
            }
        } catch (Exception e) {
            handleErrors(e);
        }
    }

    private void listGames() throws IOException, InterruptedException {
        try {
            checkIfLoggedIn();
            ListGamesResult listGamesResult = server.listGames(new ListGamesRequest(authData.authToken()));
            if (listGamesResult == null) {
                System.out.println("Error occurred while listing games");
            }
            else {
                System.out.println("Here is a list of the current games.\n");
                var games = listGamesResult.games();
                for (int i = 1; i <= games.size(); i++) {
                    var game = games.get(i-1);
                    System.out.println(String.format("%d. Game Name: %s White Username: %s Black Username: %s", i, game.gameName(), game.whiteUsername(), game.blackUsername()));
                }
            }
        } catch (Exception e) {
            handleErrors(e);
        }
    }

    private void joinGame(String[] params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException("You need to input a player color and game ID to join a game");
        }
        try {
            checkIfLoggedIn();
            String playerColor = params[1].toLowerCase();
            Integer gameID = Integer.parseInt(params[0]);
            ChessGame.TeamColor teamColor;
            if (playerColor.equals("white")) {
                teamColor = ChessGame.TeamColor.WHITE;
            }
            else {
                teamColor = ChessGame.TeamColor.BLACK;
            }
            JoinGameResult joinGameResult = server.joinGame(new JoinGameRequest(authData.authToken(), teamColor, gameID));
            if (joinGameResult == null) {
                System.out.println("Error occurred while trying to join game");
            }
            else {
                System.out.println("You have joined the game.\n");
            }
        } catch (Exception e) {
            handleErrors(e);
        }
    }

    private void logout() throws ResponseException {
        try {
            checkIfLoggedIn();
            LogoutResult logoutResult = server.logout(new LogoutRequest(authData.authToken()));
            if (logoutResult == null) {
                System.out.println("Error occurred while logging out");
            }
            else {
                System.out.println("You are logged out. Thanks for playing!\n");
                userState = State.LOGGED_OUT;
                help();
            }

        } catch(Exception e) {
            handleErrors(e);
        }
    }

    private void checkIfLoggedIn() throws ResponseException {
        if (userState != State.LOGGED_IN) {
            throw new ResponseException("You must be logged in.");
        }
    }

    private void checkIfLoggedOut() throws ResponseException {
        if (userState != State.LOGGED_OUT) {
            throw new ResponseException("You must be logged out.");
        }
    }

    private void handleErrors(Exception e) {
        if (e instanceof ResponseException) {
            System.out.println(e.getMessage());
        }
        else if (e instanceof IOException || e instanceof InterruptedException) {
            System.out.println("An error occurred");
        }
    }

}


