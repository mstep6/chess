package server;

import com.google.gson.Gson;
import io.javalin.*;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import io.javalin.http.Context;
import resultsandrequests.*;
import service.UserService;

public class Server {
    private final UserService userService = new UserService();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                // Register your endpoints and exception handlers here.
                .delete("/db", this::clearDb)
                .post("/user", this::registerUser)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser);
//                .get("/game", Server::listGames)
//                .post("/game", Server::createGame)
//                .put("/game", Server::joinGame);

    }


    private void clearDb(@NotNull Context ctx) {
        var r = Map.of();
        var json = new Gson();
        ctx.json(json.toJson(r));
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

    private void logoutUser(@NotNull Context ctx) {
        try {
            var header = new Gson().fromJson(ctx.header("authorization:"), LogoutRequest.class);
            if (header.authToken() == null) {
                ctx.status(400);
                throw new BadRequestException("Not a valid request");
            }
            else {
                LogoutResult logoutResult = userService.logout(new LogoutRequest(header.authToken()));
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(logoutResult));
            }
//
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                ctx.status(401);
            }
            System.out.println(e.getMessage());
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
