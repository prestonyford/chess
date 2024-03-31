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
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;

import javax.sound.midi.SysexMessage;
import javax.websocket.MessageHandler;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;
    private WebSocketCommunicator webSocketCommunicator;
    private String domainName;
    private MessageHandler.Whole<String> wsMessageHandler;
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
        webSocketCommunicator = new WebSocketCommunicator(domainName, wsMessageHandler);
        webSocketCommunicator.joinPlayer(new JoinPlayer(
                authToken,
                request.gameID(),
                // TODO: Refactor to use enums instead of strings in JoinGameRequest and similar
                request.playerColor().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK
        ));
    }

    public void move(int gameID, ChessMove move) throws ResponseException {
        MakeMove message = new MakeMove(
                authToken, gameID, move
        );
        webSocketCommunicator.move(message);
    }

    public ListGamesResponse listGames() throws ResponseException {
        return httpCommunicator.listGames(authToken);
    }
}
