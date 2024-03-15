package clientTests;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.LoginResponse;
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
                "By@The.Sea"
        ));
        Assertions.assertEquals("Ponyo", response.username());
    }

    @Test
    public void badRegisterAlreadyExists() throws ResponseException {
        RegisterResponse response = serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        Assertions.assertEquals("Ponyo", response.username());
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.register(new RegisterRequest(
                        "Ponyo",
                        "OnACliff",
                        "By@The.Sea"
                )),
                "Server allowed duplicate username when it shouldn't have"
        );
    }

    @Test
    public void login() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        LoginResponse response = serverFacade.login(new LoginRequest(
                "Ponyo",
                "OnACliff"
        ));
        Assertions.assertEquals("Ponyo", response.username());
    }

    @Test
    public void badLoginNoUser() throws ResponseException {
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.login(new LoginRequest(
                        "Ponyo",
                        "OnACliff"
                )),
                "Server allowed login to nonexistent user when it shouldn't have"
        );
    }

    @Test
    public void badLoginBadPassword() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.login(new LoginRequest(
                        "Ponyo",
                        "NotOnACliff"
                )),
                "Server allowed login with bad password it shouldn't have"
        );
    }

    @Test
    public void logout() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        serverFacade.login(new LoginRequest(
                "Ponyo",
                "OnACliff"
        ));
        serverFacade.logout();
    }

    @Test
    public void logoutAndLogBackIn() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        serverFacade.logout();
        serverFacade.login(new LoginRequest(
                "Ponyo",
                "OnACliff"
        ));
        serverFacade.logout();
    }

    @Test
    public void badLogoutNoAuth() throws ResponseException {
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.logout(),
                "Server allowed logout when it shouldn't have"
        );
    }

    @Test
    public void createGame() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        serverFacade.createGame(new CreateGameRequest("New Game"));
    }

    @Test
    public void badCreateGameNoAuth() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        serverFacade.logout();
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.createGame(new CreateGameRequest("New Game")),
                "Server allowed creation of game without authorization when it shouldn't have"
        );
    }

    @Test
    public void joinGame() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(
                "New Game"
        ));
        serverFacade.joinGame(new JoinGameRequest("WHITE", response.gameID()));
    }

    @Test
    public void badJoinGameNoAuth() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(
                "New Game"
        ));
        serverFacade.logout();
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.joinGame(new JoinGameRequest("WHITE", response.gameID())),
                "Server allowed joining a game without authorization when it shouldn't have"
        );
    }

    @Test
    public void badJoinGameBadID() throws ResponseException {
        serverFacade.register(new RegisterRequest(
                "Ponyo",
                "OnACliff",
                "By@The.Sea"
        ));
        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(
                "New Game"
        ));
        Assertions.assertThrows(
                ResponseException.class,
                () -> serverFacade.joinGame(new JoinGameRequest("WHITE", response.gameID() + 1)),
                "Server allowed joining a nonexistent game when it shouldn't have"
        );
    }
}
