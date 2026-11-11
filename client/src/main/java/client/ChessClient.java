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
        System.out.println("\uD83D\uDC51 Welcome to 240 Chess! Type 'help' to get started. \uD83D\uDC51" + EscapeSequences.SET_TEXT_COLOR_MAGENTA);
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
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void drawLabeledColumns(String[] columns) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        for (var letter : columns) {
            System.out.print(" " + letter + " ");
        }
        System.out.print("   " + EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    public String switchColors(String currColor) {
        if (currColor.equals(EscapeSequences.SET_BG_COLOR_DARK_GREEN)) {
            return EscapeSequences.SET_BG_COLOR_WHITE;
        }
        return EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    }

    public void addPawnsToBoard(String pieceColor, String perspective) {
        String squareColor;
        if (pieceColor.equals(EscapeSequences.SET_TEXT_COLOR_BLACK) && perspective.equals("white")) {
            squareColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        }
        else if (pieceColor.equals(EscapeSequences.SET_TEXT_COLOR_BLACK) && perspective.equals("black")) {
            squareColor = EscapeSequences.SET_BG_COLOR_WHITE;
        }
        else if (pieceColor.equals(EscapeSequences.SET_TEXT_COLOR_BLUE) && perspective.equals("white"))  {
            squareColor = EscapeSequences.SET_BG_COLOR_WHITE;
        }
        else {
            squareColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        }
        for (int i = 0; i < 8; i++) {
            System.out.print(squareColor +pieceColor + " P ");
            squareColor = switchColors(squareColor);
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
    }

    public void addPieces(String pieceColor, String currColor, String perspective) {
        String[] black = {" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};
        String[] white = {" R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R "};
        String[] order;
        if (perspective.equals("black")) {
            order = black;
        }
        else {
            order = white;
        }
        System.out.print(pieceColor + currColor + order[0]);
        currColor = switchColors(currColor);
        System.out.print(pieceColor + currColor + order[1]);
        currColor = switchColors(currColor);
        System.out.print(pieceColor + currColor + order[2]);
        currColor = switchColors(currColor);
        System.out.print(pieceColor + currColor + order[3]);
        currColor = switchColors(currColor);
        System.out.print(pieceColor + currColor + order[4]);
        currColor = switchColors(currColor);
        System.out.print(pieceColor + currColor + order[5]);
        currColor = switchColors(currColor);
        System.out.print(pieceColor  + currColor + order[6]);
        currColor = switchColors(currColor);
        System.out.print(pieceColor + currColor + order[7]);
    }

    public void drawBoardSquares(int[] rows, String perspective) {
        String currColor = EscapeSequences.SET_BG_COLOR_WHITE;

        for (int i = 0; i < 8; i++) {
            System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print("\n" + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + rows[i] + " ");
            if (i == 1 && perspective.equals("black")) {
                addPawnsToBoard(EscapeSequences.SET_TEXT_COLOR_BLUE, "black");
            }
            else if (i == 6 && perspective.equals("white")) {
                addPawnsToBoard(EscapeSequences.SET_TEXT_COLOR_BLUE, "white");
            }
            else if (i == 1 && perspective.equals("white")) {
                addPawnsToBoard(EscapeSequences.SET_TEXT_COLOR_BLACK, "white");
            }
            else if (i == 6 && perspective.equals("black")) {
                addPawnsToBoard(EscapeSequences.SET_TEXT_COLOR_BLACK, "black");
            }
            else if (i == 0 && perspective.equals("black")) {
                addPieces(EscapeSequences.SET_TEXT_COLOR_BLUE, currColor, perspective);
            }
            else if (i == 7 && perspective.equals("white")) {
                addPieces(EscapeSequences.SET_TEXT_COLOR_BLUE, currColor, perspective);
            }
            else if (i == 0 && perspective.equals("white")) {
                addPieces(EscapeSequences.SET_TEXT_COLOR_BLACK, currColor, perspective);
            }
            else if (i == 7 && perspective.equals("black")) {
                addPieces(EscapeSequences.SET_TEXT_COLOR_BLACK, currColor, perspective);
            }
            else {
                for (int j = 0; j < 8; j++) {
                System.out.print(currColor + "   ");
                currColor = switchColors(currColor);
                }
            }

            System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + rows[i] + " ");
            currColor = switchColors(currColor);
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    public void drawBoard(String color) {
        String[] columns = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        int[] rows = new int[]{8, 7, 6, 5, 4, 3, 2, 1};
        if (color.toLowerCase().equals("black")) {
            columns = new String[] {"h", "g", "f", "e", "d", "c", "b", "a"};
            rows = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        }
        drawLabeledColumns(columns);
        drawBoardSquares(rows, color.toLowerCase());
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print("\n");
        drawLabeledColumns(columns);
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
        if (params.length == 0) {
            throw new ResponseException("You need to input a game name to create a game");
        }
        String gameName = String.join(" ", params);
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
                    System.out.println(String.format("%d. Game Name: %s White Username: %s Black Username: %s",
                            i, game.gameName(), game.whiteUsername(), game.blackUsername()));
                }
            }
        } catch (Exception e) {
            handleErrors(e);
        }
    }

    private void joinGame(String[] params) throws ResponseException {
        Integer gameID;
        if (params.length != 2) {
            throw new ResponseException("You need to input a game ID and player color to join a game");
        }
        try {
            checkIfLoggedIn();
            try {
                gameID = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new ResponseException("You need to input a game ID and player color to join a game");
            }
            String playerColor = params[1].toLowerCase();
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
                drawBoard(playerColor);
            }
        } catch (Exception e) {
            handleErrors(e);
        }
    }

    private void observeGame(String[] params) {
        if (params.length != 1) {
            System.out.println("You must specify which game you want to observe.");
        }
        else {
            drawBoard(EscapeSequences.SET_BG_COLOR_WHITE);
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
        else {
            System.out.println("An error occurred");
        }
    }

}


