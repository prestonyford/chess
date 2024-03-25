package client.http;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.ListGamesResponse;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import client.exception.ResponseException;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpCommunicator {
    private final String serverUrl;

    public HttpCommunicator(String domainName) throws URISyntaxException, MalformedURLException {
        this.serverUrl = "http://" + domainName;
    }

    public void clearDB() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        return makeRequest("POST", "/user", null, request, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        return makeRequest("POST", "/session", null, request, LoginResponse.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", authToken, null, null);
    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) throws ResponseException {
        return makeRequest("POST", "/game", authToken, request, CreateGameResponse.class);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws ResponseException {
        makeRequest("PUT", "/game", authToken, request, JoinGameRequest.class);
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        return makeRequest("GET", "/game", authToken, null, ListGamesResponse.class);
    }

    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> responseClass) throws ResponseException {
        try {
            HttpURLConnection http = (HttpURLConnection) new URI(serverUrl + path).toURL().openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeAuthHeader(http, authToken);
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

    private void writeAuthHeader(HttpURLConnection http, String authToken) {
        http.addRequestProperty("Authorization", authToken);
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
