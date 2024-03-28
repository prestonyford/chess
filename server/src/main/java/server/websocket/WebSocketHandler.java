package server.websocket;

import chess.dataModel.AuthData;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final DataAccess db;

    public WebSocketHandler(DataAccess db) {
        this.db = db;
    }

    // Mapping of gameID to its ConnectionManager
    private final ConcurrentHashMap<Integer, ConnectionManager> gameRooms = new ConcurrentHashMap<Integer, ConnectionManager>();

    @OnWebSocketMessage
    public void onWebSocketText(Session session, String s) throws DataAccessException {
        UserGameCommand command = new Gson().fromJson(s, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(s, JoinPlayerCommand.class);
                if (!gameRooms.containsKey(joinPlayerCommand.getGameID())) {
                    gameRooms.put(joinPlayerCommand.getGameID(), new ConnectionManager());
                }
                AuthData user = db.getAuth(joinPlayerCommand.getAuthString());
                gameRooms.get(joinPlayerCommand.getGameID()).add(user.username(), session);
                break;
        }
    }
}
