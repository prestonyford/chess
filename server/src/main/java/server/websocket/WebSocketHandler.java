package server.websocket;

import chess.ChessGame;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import javax.sound.midi.SysexMessage;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final DataAccess db;

    // Mapping of gameID to its ConnectionManager
    private final ConcurrentHashMap<Integer, ConnectionManager> gameRooms = new ConcurrentHashMap<Integer, ConnectionManager>();

    public WebSocketHandler(DataAccess db) {
        this.db = db;
    }

    private void addConnectionToRoom(int gameID, Connection connection) {
        if (!gameRooms.containsKey(gameID)) {
            gameRooms.put(gameID, new ConnectionManager());
        }
        gameRooms.get(gameID).add(connection);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String s) throws DataAccessException, IOException {
        UserGameCommand command = new Gson().fromJson(s, UserGameCommand.class);
        AuthData user = db.getAuth(command.getAuthString());
        Connection connection = new Connection(user.username(), session);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(connection, new Gson().fromJson(s, JoinPlayer.class));
            case JOIN_OBSERVER -> joinObserver(connection, new Gson().fromJson(s, JoinObserver.class));
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) throws IOException {
        System.out.println(error.getMessage());
        session.getRemote().sendString(
                new Gson().toJson(new Error(
                        error.getMessage()
                ))
        );
    }

    private void joinPlayer(Connection connection, JoinPlayer message) throws WebSocketException {
        try {
            GameData game = db.getGame(message.getGameID());
            if (
                    (message.getPlayerColor() == ChessGame.TeamColor.WHITE && !Objects.equals(game.whiteUsername(), connection.visitorName)) ||
                            (message.getPlayerColor() == ChessGame.TeamColor.BLACK && !Objects.equals(game.blackUsername(), connection.visitorName))
            ) {
                throw new WebSocketException("Position is occupied");
            }

            addConnectionToRoom(message.getGameID(), connection);
            connection.send(new Gson().toJson(new LoadGame(game)));
            gameRooms.get(message.getGameID()).broadcast(connection.visitorName, new Notification(
                    connection.visitorName + " joined as " + message.getPlayerColor().name()
            ));
        } catch (IOException | DataAccessException ex) {
            throw new WebSocketException(ex.getMessage());
        }
    }

    private void joinObserver(Connection connection, JoinObserver message) throws WebSocketException {
        try {
            GameData game = db.getGame(message.getGameID());
            if (game == null) {
                throw new WebSocketException("Nonexistent gameID");
            }
            addConnectionToRoom(message.getGameID(), connection);
            connection.send(new Gson().toJson(new LoadGame(game)));
            gameRooms.get(message.getGameID()).broadcast(connection.visitorName, new Notification(
                    connection.visitorName + " joined as an observer"
            ));
        } catch (IOException | DataAccessException ex) {
            throw new WebSocketException(ex.getMessage());
        }
    }
}
