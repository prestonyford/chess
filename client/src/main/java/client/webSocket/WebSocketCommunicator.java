package client.webSocket;

import client.exception.ResponseException;
import com.google.gson.Gson;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    public Session session;

    public WebSocketCommunicator(String domainName, MessageHandler.Whole<String> wsMessageHandler) throws ResponseException {
        try {
            URI uri = new URI(String.format("ws://%s/connect", domainName));
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
            this.session.addMessageHandler(wsMessageHandler);
        } catch (URISyntaxException | DeploymentException | IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(JoinPlayer message) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(message));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void move(MakeMove message) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(message));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private void send(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
