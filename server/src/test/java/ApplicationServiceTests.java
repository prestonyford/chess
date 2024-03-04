import chess.dataModel.GameData;
import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.ListGamesResponse;
import chess.dataModel.response.RegisterResponse;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.SQLDataAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import service.ApplicationService;
import service.GameService;
import service.UserService;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationServiceTests {
    private static final DataAccess db;

    static {
        try {
            db = new MemoryDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final UserService userService = new UserService(db);
    private static final GameService gameService = new GameService(db);
    private static final ApplicationService applicationService = new ApplicationService(db);

    @Test
    @DisplayName("Clear database")
    public void clearDatabase() {
        // Add a user
        try {
            RegisterResponse registerResponse = userService.register(new RegisterRequest(
                    "Navia",
                    "Navia's password",
                    "navia@email.com")
            );
            gameService.createGame(registerResponse.authToken(), new CreateGameRequest("Navia's chess game"));
            gameService.createGame(registerResponse.authToken(), new CreateGameRequest("Navia's chess game 2"));
            userService.logout(registerResponse.authToken());
            userService.login(new LoginRequest("Navia", "Navia's password"));
        } catch (Exception ex) {
            throw new TestException("Could not add data to database");
        }

        try {
            applicationService.bigRedButton();
        } catch (Exception ex) {
            throw new TestException("Could not clear database: " + ex.getMessage());
        }


        // Assert that the user and auth was removed
        assertThrows(
                ServiceException.class,
                () -> userService.login(new LoginRequest("Navia", "Navia's password")),
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
