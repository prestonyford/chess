package client.webSocket;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator {
    public Session session;

    public WebSocketCommunicator(String domainName) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI(String.format("ws://%s/connect", domainName));
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }
}
