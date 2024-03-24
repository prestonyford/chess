package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

public class WebSocketHandler implements WebSocketListener {
    private final ConnectionManager connections = new ConnectionManager();

//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) throws IOException {
//
//    }

    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i1) {

    }

    @Override
    public void onWebSocketText(String s) {

    }

    @Override
    public void onWebSocketClose(int i, String s) {

    }

    @Override
    public void onWebSocketConnect(Session session) {
        System.out.println("New session!");
    }

    @Override
    public void onWebSocketError(Throwable throwable) {

    }
}
