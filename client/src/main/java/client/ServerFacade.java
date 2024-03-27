package client;

import java.io.IOException;
import java.net.*;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.ListGamesResponse;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import client.http.HttpCommunicator;
import client.exception.ResponseException;
import client.webSocket.WebSocketCommunicator;

import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;
    private final WebSocketCommunicator webSocketCommunicator;
    private String authToken;

    public ServerFacade(String domainName, MessageHandler.Whole<String> wsMessageHandler) throws URISyntaxException, IOException, DeploymentException {
        httpCommunicator = new HttpCommunicator(domainName);
        webSocketCommunicator = new WebSocketCommunicator(domainName, wsMessageHandler);
    }

    public void clearDB() throws ResponseException {
        httpCommunicator.clearDB();
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        RegisterResponse response = httpCommunicator.register(request);
        authToken = response.authToken();
        return response;
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        LoginResponse response = httpCommunicator.login(request);
        authToken = response.authToken();
        return response;
    }

    public void logout() throws ResponseException {
        httpCommunicator.logout(authToken);
        authToken = null;
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        return httpCommunicator.createGame(request, authToken);
    }

    public void joinGame(JoinGameRequest request) throws ResponseException {
        httpCommunicator.joinGame(request, authToken);
    }

    public ListGamesResponse listGames() throws ResponseException {
        return httpCommunicator.listGames(authToken);
    }
}
