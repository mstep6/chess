package server;

import com.google.gson.Gson;
import io.javalin.*;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import io.javalin.http.Context;
import resultsandrequests.*;
import service.DeleteService;
import service.GameService;
import service.UserService;

public class Server {
    private final UserService userService = new UserService();
    private final DeleteService deleteService = new DeleteService();
    private final GameService gameService = new GameService();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                // Register your endpoints and exception handlers here.
                .delete("/db", this::clearDb)
                .post("/user", this::registerUser)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame);
    }

    private void joinGame(Context ctx) {
        try {
            var authToken = ctx.header("authorization");
            var body = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
            if (authToken == null) {
                throw new UnauthorizedException("Error: unauthorized");
            }
            if (body == null || body.playerColor() == null || body.gameID() == null) {
                throw new BadRequestException("Error: bad request");
            }
            JoinGameResult joinGameResult = gameService.joinGame(new JoinGameRequest(authToken, body.playerColor(), body.gameID()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(joinGameResult));
        }
        catch (Exception e) {
            if (e instanceof BadRequestException) {
                ctx.status(400);
            }
            if (e instanceof UnauthorizedException) {
                ctx.status(401);
            }
            else if (e instanceof GameDoesNotExistException || e instanceof AlreadyTakenException) {
                ctx.status(403);
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void createGame(Context ctx) {
        try {
            var authToken = ctx.header("authorization");
            var body = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
            if (authToken == null) {
                throw new UnauthorizedException("Error: unauthorized");
            }
            if (body == null || body.gameName() == null) {
                throw new BadRequestException("Error: bad request");
            }
            CreateGameResult createGameResult = gameService.createGame(new CreateGameRequest(authToken, body.gameName()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(createGameResult));

        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                ctx.status(401);
            }
            else if (e instanceof BadRequestException) {
                ctx.status(400);
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));

        }
    }

    private void listGames(Context ctx) {
        try {
            var authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401);
                throw new UnauthorizedException("Error: unauthorized");
            }
            else {
                ListGamesResult listGamesResult = gameService.listAllGames(new ListGamesRequest(authToken));
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(listGamesResult));
            }
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                ctx.status(401);
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }


    private void registerUser(Context ctx) {
        try {
            var body = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            if (body == null || body.username() == null || body.password() == null || body.email() == null) {
                ctx.status(400);
                throw new BadRequestException("Error: unauthorized");
            }
            RegisterResult registerResult = userService.register(new RegisterRequest(body.username(), body.password(), body.email()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(registerResult));
        }
        catch (Exception e) {
            if (e instanceof AlreadyTakenException) {
                ctx.status(403);
            }
            else if (e instanceof BadRequestException) {
                ctx.status(400);
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void loginUser(Context ctx) {
        try {
            var body = new Gson().fromJson(ctx.body(), LoginRequest.class);
            if (body == null || body.username() == null || body.password() == null) {
                ctx.status(400);
                throw new BadRequestException("Error: unauthorized");
            }
            LoginResult loginResult = userService.login(new LoginRequest(body.username(), body.password()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(loginResult));
        }
        catch (Exception e) {
            if (e instanceof WrongPasswordException || e instanceof UnauthorizedException) {
                ctx.status(401);
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void logoutUser(Context ctx) {
        try {
            var authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401);
                throw new UnauthorizedException("Error: unauthorized");
            }
            else {
                LogoutResult logoutResult = userService.logout(new LogoutRequest(authToken));
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(logoutResult));
            }
        }
        catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                ctx.status(401);
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    public void clearDb(Context ctx) {
        userService.clearDatabase();
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
