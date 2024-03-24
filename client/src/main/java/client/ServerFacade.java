package client;

import java.net.*;
import java.io.*;
import java.util.Map;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.ListGamesResponse;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import com.google.gson.Gson;
import client.exception.ResponseException;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String domainName) throws URISyntaxException, MalformedURLException {
        this.serverUrl = "http://" + domainName;
    }

    public void clearDB() throws ResponseException {
        makeRequest("DELETE", "/db", null, null);
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        RegisterResponse response = makeRequest("POST", "/user", request, RegisterResponse.class);
        authToken = response.authToken();
        return response;
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        LoginResponse response = makeRequest("POST", "/session", request, LoginResponse.class);
        authToken = response.authToken();
        return response;
    }

    public void logout() throws ResponseException {
        makeRequest("DELETE", "/session", null, null);
        authToken = null;
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        return makeRequest("POST", "/game", request, CreateGameResponse.class);
    }

    public void joinGame(JoinGameRequest request) throws ResponseException {
        makeRequest("PUT", "/game", request, JoinGameRequest.class);
    }

    public ListGamesResponse listGames() throws ResponseException {
        return makeRequest("GET", "/game", null, ListGamesResponse.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            HttpURLConnection http = (HttpURLConnection) new URI(serverUrl + path).toURL().openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeAuthHeader(http);
            writeBody(http, request);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private void writeBody(HttpURLConnection http, Object request) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void writeAuthHeader(HttpURLConnection http) {
        http.addRequestProperty("Authorization", this.authToken);
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        String message = String.format("Failure(%d)", status);
        if (!isSuccessful(status)) {
            var body = readBody(http, Map.class);
            if (body.containsKey("message")) {
                message = message + ": " + body.get("message");
            }
            throw new ResponseException(status, message);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        InputStream stream;
        if (isSuccessful(http.getResponseCode())) {
            stream = http.getInputStream();
        } else {
            stream = http.getErrorStream();
        }
        if (http.getContentLength() <= 0) {
            try (InputStream respBody = stream) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private static boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
