package client;

import com.google.gson.Gson;
import model.AuthData;
import resultsandrequests.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;
    private AuthData authData;
    private State userState = State.LOGGED_OUT;

    public AuthData getAuthData() {
        return authData;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }



    public State getUserState() {
        return userState;
    }

    public void setUserState(State userState) {
        this.userState = userState;
    }



    public ServerFacade(String url) {
        serverURL = url;
    }

    public void clearDb() throws IOException, InterruptedException {
        var request = buildRequest("DELETE", "/db", null);
        var response = sendRequest(request);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws IOException, InterruptedException {
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request);
        RegisterResult registerResult = handleResponse(response, RegisterResult.class);
        if (registerResult != null) {
            authData = new AuthData(registerRequest.username(), registerResult.authToken());
            userState = State.LOGGED_IN;
        }
        return registerResult;
    }

    public LoginResult login(LoginRequest loginRequest) throws IOException, InterruptedException {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        LoginResult loginResult = handleResponse(response, LoginResult.class);
        if (loginResult != null) {
            authData = new AuthData(loginResult.username(), loginResult.authToken());
            userState = State.LOGGED_IN;
        }
        return loginResult;
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws IOException, InterruptedException {
        var request = buildRequest("DELETE", "/session", logoutRequest);
        var response = sendRequest(request);
        if (response != null) {
            authData = null;
            userState = State.LOGGED_OUT;
        }
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
        if (userState == State.LOGGED_IN) {
            request.setHeader("authorization", authData.authToken());
        }
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

