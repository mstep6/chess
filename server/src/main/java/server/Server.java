package server;

import com.google.gson.Gson;
import io.javalin.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import io.javalin.http.Context;
import resultsandrequests.BadRequestException;
import resultsandrequests.RegisterRequest;
import resultsandrequests.RegisterResult;
import service.UserService;

public class Server {
    private final UserService userService = new UserService();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                // Register your endpoints and exception handlers here.
                .delete("/db", this::clearDb)
                .post("/user", this::registerUser);
//                .post("/session", Server::loginUser)
//                .delete("/session", Server::logoutUser)
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
                throw new BadRequestException("Not a valid request");
            }
            if (body.username() == null || body.password() == null || body.email() == null) {
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

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
