package client.webSocket;

import webSocketMessages.serverMessages.ServerMessage;

public interface WebSocketMessageHandler {
    public void onServerMessage(ServerMessage serverMessage);
}
