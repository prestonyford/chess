package serviceTests;

import chess.dataModel.GameData;
import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.ListGamesResponse;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import service.ApplicationService;
import service.GameService;
import service.UserService;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationServiceTests {
    @Test
    @DisplayName("Clear database")
    public void clearDatabase() {
        var userService = UserService.getInstance();
        var gameService = GameService.getInstance();
        var applicationService = ApplicationService.getInstance();

        // Add a user
        try {
            RegisterResponse registerResponse = userService.register(new RegisterRequest(
                    "Steve",
                    "Steve's password",
                    "steve@email.com")
            );
            gameService.createGame(registerResponse.authToken(), new CreateGameRequest("Steve's chess game"));
            gameService.createGame(registerResponse.authToken(), new CreateGameRequest("Steve's chess game 2"));
            userService.logout(registerResponse.authToken());
            userService.login(new LoginRequest("Steve", "Steve's password"));
        } catch (Exception ex) {
            throw new TestException("Could not add data to database");
        }

        applicationService.bigRedButton();

        // Assert that the user and auth was removed
        assertThrows(
                ServiceException.class,
                () -> userService.login(new LoginRequest("Steve", "Steve's password")),
                "Expected login to throw ServiceException with Error: unauthorized, but it didn't"
        );

        // Assert that the games were removed
        try {
            RegisterResponse registerResponse = userService.register(new RegisterRequest("temp", "temp", "temp"));

            assertArrayEquals(
                    new ListGamesResponse(new GameData[]{}).games(),
                    gameService.listGames(registerResponse.authToken()).games(),
                    "List games was not empty when it should have been"
            );

        } catch (Exception ex) {
            throw new TestException("Could not add data to database after clear");
        }

    }
}
