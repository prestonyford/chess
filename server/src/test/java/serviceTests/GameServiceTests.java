package serviceTests;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
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
    private final UserService userService = UserService.getInstance();
    private final GameService gameService = GameService.getInstance();

    @Test
    @Order(1)
    @DisplayName("Create game")
    public void createGame() throws TestException {
        RegisterResponse registerResponse;
        try {
            registerResponse = userService.register(new RegisterRequest(
                    "Steve",
                    "Steve's password",
                    "steve@email.com"
            ));
        } catch (Exception ex) {
            throw new TestException("Could not create an account");
        }

        try {
            gameService.createGame(registerResponse.authToken(), new CreateGameRequest("gameA"));
        } catch (Exception ex) {
            throw new TestException("Could not create a game");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Create game with bad name")
    public void badCreateGame() throws TestException {
        LoginResponse loginResponse;
        try {
            loginResponse = userService.login(new LoginRequest(
                    "Steve",
                    "Steve's password"
            ));
        } catch (Exception ex) {
            throw new TestException("Could not login to account");
        }

        assertThrows(
                ServiceException.class,
                () -> gameService.createGame(loginResponse.authToken(), new CreateGameRequest("")),
                "Service allowed an empty string game name when it shouldn't have"
        );
    }
}
