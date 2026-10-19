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
            if (body == null) {
                ctx.status(400);
                throw new BadRequestException("Not a valid request");
            }
            if (body.username() == null || body.password() == null || body.email() == null) {
                ctx.status(400);
                throw new BadRequestException("Not a valid request");
            }
            RegisterResult registerResult = userService.register(new RegisterRequest(body.username(), body.password(), body.email()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(registerResult));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void loginUser(Context ctx) {
        try {
            var body = new Gson().fromJson(ctx.body(), LoginRequest.class);
            if (body == null || body.username() == null || body.password() == null) {
                ctx.status(400);
                throw new BadRequestException("Not a valid request");
            }
            LoginResult loginResult = userService.login(new LoginRequest(body.username(), body.password()));
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(loginResult));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void logoutUser(@NotNull Context context) {

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
