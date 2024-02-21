package serviceTests;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import service.GameService;
import service.UserService;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {
    private static final UserService userService = UserService.getInstance();
    private static final GameService gameService = GameService.getInstance();
    private static RegisterResponse registerResponse;
    private static CreateGameResponse createGameResponse;

    @BeforeAll
    public static void init() {
        try {
            registerResponse = userService.register(new RegisterRequest(
                    "Steve",
                    "Steve's password",
                    "steve@email.com"
            ));
        } catch (Exception ex) {
            throw new TestException("Could not create an account");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Create game")
    public void createGame() throws TestException {
        try {
            createGameResponse = gameService.createGame(registerResponse.authToken(), new CreateGameRequest("gameA"));
        } catch (Exception ex) {
            throw new TestException("Could not create a game");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Create game with bad name")
    public void badCreateGame() throws TestException {
        assertThrows(
                ServiceException.class,
                () -> gameService.createGame(registerResponse.authToken(), new CreateGameRequest("")),
                "Service allowed an empty string game name when it shouldn't have"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Join game")
    public void joinGame() throws TestException {
        try {
            gameService.joinGame(
                    registerResponse.authToken(),
                    new JoinGameRequest("WHITE", createGameResponse.gameID())
            );
        } catch (Exception ex) {
            throw new TestException("Could not join game");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Attempt to join game as white that already has white")
    public void badJoinGame() throws TestException {
        RegisterResponse registerResponsePlayer2;
        try {
            registerResponsePlayer2 = userService.register(
                    new RegisterRequest(
                            "Player2",
                            "player2password",
                            "player2@email.com"
                    )
            );
        } catch (Exception ex) {
            throw new TestException("Could not create player 2 account");
        }

        assertThrows(
                ServiceException.class,
                () -> gameService.joinGame(
                        registerResponsePlayer2.authToken(),
                        new JoinGameRequest("WHITE", createGameResponse.gameID())
                ),
                "Game service allowed a player to join white when there was already a white player"
        );
    }

    @Test
    @Order(5)
    @DisplayName("List games")
    public void listGames() throws TestException {
        
    }
}
