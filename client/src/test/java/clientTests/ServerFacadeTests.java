package clientTests;

import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.RegisterResponse;
import client.ServerFacade;
import client.exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void beforeEach() throws ResponseException {
        serverFacade.clearDB();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void register() throws ResponseException {
        RegisterResponse response = serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "ByTheSea"
        ));
        Assertions.assertEquals("Ponyo", response.username());
    }

    @Test
    public void badRegister() throws ResponseException {
        RegisterResponse response = serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "ByTheSea"
        ));
        Assertions.assertEquals("Ponyo", response.username());
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.register(new RegisterRequest(
                        "Ponyo",
                        "OnACliff",
                        "ByTheSea"
                )),
                "Server allowed duplicate username when it shouldn't have"
        );
    }
}
