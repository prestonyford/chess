package server.websocket;

import webSocketMessages.serverMessages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GameRoom {
    private final ConnectionManager connections = new ConnectionManager();
    private final int gameID;
    private Connection whitePlayer;
    private Connection blackPlayer;
    private Collection<Connection> observers;

    public GameRoom(int gameID) {
        this.gameID = gameID;
    }

    public void broadcast(String excludeVisitorName, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        Collection<Connection> users = new ArrayList<>(observers);
        users.addAll(List.of(whitePlayer, blackPlayer));

        for (var c : users) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}
