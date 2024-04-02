package client;

import chess.ChessGame;
import chess.ChessMove;
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
import webSocketMessages.userCommands.*;

import javax.websocket.MessageHandler;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;
    private WebSocketCommunicator webSocketCommunicator;
    private final String domainName;
    private final MessageHandler.Whole<String> wsMessageHandler;
    private String authToken;

    public ServerFacade(String domainName, MessageHandler.Whole<String> wsMessageHandler) {
        this.domainName = domainName;
        this.wsMessageHandler = wsMessageHandler;
        httpCommunicator = new HttpCommunicator(domainName);
    }

    public void clearDB() throws ResponseException {
        httpCommunicator.clearDB();
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        RegisterResponse response = httpCommunicator.register(request);
        authToken = response.authToken();
        webSocketCommunicator = new WebSocketCommunicator(domainName, wsMessageHandler);
        return response;
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        LoginResponse response = httpCommunicator.login(request);
        authToken = response.authToken();
        webSocketCommunicator = new WebSocketCommunicator(domainName, wsMessageHandler);
        return response;
    }

    public void logout() throws ResponseException {
        httpCommunicator.logout(authToken);
        webSocketCommunicator = null;
        authToken = null;
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        return httpCommunicator.createGame(request, authToken);
    }

    public void joinGame(JoinGameRequest request) throws ResponseException {
        httpCommunicator.joinGame(request, authToken);
        webSocketCommunicator.sendMessage(new JoinPlayer(
                authToken,
                request.gameID(),
                // TODO: Refactor to use enums instead of strings in JoinGameRequest and similar
                request.playerColor().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK
        ));
    }

    public ListGamesResponse listGames() throws ResponseException {
        return httpCommunicator.listGames(authToken);
    }

    public void makeMove(int gameID, ChessMove move) throws ResponseException {
        MakeMove message = new MakeMove(
                authToken, gameID, move
        );
        webSocketCommunicator.sendMessage(message);
    }

    public void observeGame(int gameID) throws ResponseException {
        webSocketCommunicator.sendMessage(new JoinObserver(
                authToken,
                gameID
        ));
    }

    public void leaveGame(int gameID) throws ResponseException {
        webSocketCommunicator.sendMessage(new Leave(
                authToken,
                gameID
        ));
    }

    public void resign(int gameID) throws ResponseException {
        webSocketCommunicator.sendMessage(new Resign(
                authToken,
                gameID
        ));
    }
}
