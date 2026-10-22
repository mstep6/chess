package server;

import com.google.gson.Gson;
import exceptions.*;
import io.javalin.*;
import java.util.Map;
import io.javalin.http.Context;
import resultsandrequests.*;
import service.GameService;
import service.UserService;

public class Server {
    private final UserService userService = new UserService();
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

    private void setStatus(int status, Context ctx) {
        ctx.status(status);
    }

    private void convertErrorToJson(Context ctx, Exception e) {
        ctx.contentType("application/json");
        ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
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
                setStatus(400, ctx);
            }
            if (e instanceof UnauthorizedException) {
                setStatus(401, ctx);
            }
            else if (e instanceof GameDoesNotExistException || e instanceof AlreadyTakenException) {
                setStatus(403, ctx);
            }
            convertErrorToJson(ctx, e);
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
                setStatus(401, ctx);
            }
            else if (e instanceof BadRequestException) {
                setStatus(400, ctx);
            }
            convertErrorToJson(ctx, e);
        }
    }

    private void listGames(Context ctx) {
        try {
            var authToken = ctx.header("authorization");
            if (authToken == null) {
                setStatus(401, ctx);
                throw new UnauthorizedException("Error: unauthorized");
            }
            else {
                ListGamesResult listGamesResult = gameService.listAllGames(new ListGamesRequest(authToken));
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(listGamesResult));
            }
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                setStatus(401, ctx);
            }
            convertErrorToJson(ctx, e);
        }
    }


    private void registerUser(Context ctx) {
        try {
            var body = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            if (body == null || body.username() == null || body.password() == null || body.email() == null) {
                setStatus(400, ctx);
                throw new BadRequestException("Error: unauthorized");
            }
            RegisterResult registerResult = userService.register(new RegisterRequest(body.username(), body.password(), body.email()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(registerResult));
        }
        catch (Exception e) {
            if (e instanceof AlreadyTakenException) {
                setStatus(403, ctx);
            }
            else if (e instanceof BadRequestException) {
                setStatus(400, ctx);
            }
            convertErrorToJson(ctx, e);
        }
    }

    private void loginUser(Context ctx) {
        try {
            var body = new Gson().fromJson(ctx.body(), LoginRequest.class);
            if (body == null || body.username() == null || body.password() == null) {
                setStatus(400, ctx);
                throw new BadRequestException("Error: unauthorized");
            }
            LoginResult loginResult = userService.login(new LoginRequest(body.username(), body.password()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(loginResult));
        }
        catch (Exception e) {
            if (e instanceof WrongPasswordException || e instanceof UnauthorizedException) {
                setStatus(401, ctx);
            }
            convertErrorToJson(ctx, e);
        }
    }

    private void logoutUser(Context ctx) {
        try {
            var authToken = ctx.header("authorization");
            if (authToken == null) {
                setStatus(401, ctx);
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
                setStatus(401, ctx);
            }
            convertErrorToJson(ctx, e);
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
