package client;

import com.google.gson.Gson;
import resultsandrequests.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws IOException, InterruptedException {
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws IOException, InterruptedException {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws IOException, InterruptedException {
        var request = buildRequest("DELETE", "/session", logoutRequest);
        var response = sendRequest(request);
        return handleResponse(response, LogoutResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws IOException, InterruptedException {
        var request = buildRequest("GET", "/game", listGamesRequest);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws IOException, InterruptedException {
        var request = buildRequest("POST", "/game", createGameRequest);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws IOException, InterruptedException {
        var request = buildRequest("PUT", "/game", joinGameRequest);
        var response = sendRequest(request);
        return handleResponse(response, JoinGameResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
//        else {
//            request.setHeader("authorization", )
//        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        }
        else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            if (status == 400) {
                System.out.println("Error: Bad request");
            }
            else if (status == 401) {
                System.out.println("Error: Unauthorized");
            }
            else if (status == 403) {
                System.out.println("Error: Forbidden");
            }
            else if (status > 499) {
                System.out.println("An error occurred");
            }
        }
        else {
            if (responseClass != null) {
                return new Gson().fromJson(response.body(), responseClass);
            }
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        if (status / 100 == 2) {
            return true;
        }
        return false;
    }
}

