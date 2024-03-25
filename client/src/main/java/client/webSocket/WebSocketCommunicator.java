package client.webSocket;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    public Session session;

    public WebSocketCommunicator(String domainName) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI(String.format("ws://%s/connect", domainName));
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    public void send(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
