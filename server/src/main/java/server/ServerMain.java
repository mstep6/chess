package server;

import chess.*;
import dataaccess.DataAccessException;

import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) {
        var port = 8080;
        Server server = new Server();
        server.run(port);

        System.out.println("♕ 240 Chess Server running on port " + port);

    }
}
