import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.SQLDataAccess;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import service.GameService;
import service.UserService;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    private static final DataAccess db = new MemoryDataAccess();
    private static final UserService userService = new UserService(db);
    private static final GameService gameService = new GameService(db);

    @Test
    @Order(1)
    @DisplayName("Register")
    public void register() throws TestException {
        try {
            RegisterResponse registerResponse = userService.register(
                    new RegisterRequest("pyford", "i_love_cs240", "preston.y.ford@gmail.com")
            );
            assertNotNull(registerResponse);
        } catch (Exception ex) {
            throw new TestException("Could not register: " + ex.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Re-register")
    public void badRegister() throws TestException {
        try {
            assertThrows(
                    ServiceException.class,
                    () -> userService.register(
                            new RegisterRequest("pyford", "i_love_cs240", "preston.y.ford@gmail.com")
                    ),
                    "Did not throw ServiceException Error: already taken"
            );
        } catch (Exception ex) {
            throw new TestException("Could not register");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Login")
    public void login() throws TestException {
        try {
            LoginResponse loginResponse = userService.login(
                    new LoginRequest("pyford", "i_love_cs240")
            );
            assertNotNull(loginResponse);
        } catch (Exception ex) {
            throw new TestException("Could not login");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Login with bad username")
    public void badLoginUser() throws TestException {
        try {
            // Bad username
            assertThrows(
                    ServiceException.class,
                    () -> userService.login(new LoginRequest("imposter", "amogus")),
                    "Accepted bad username when it shouldn't have"
            );
        } catch (Exception ex) {
            throw new TestException("Could not login");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Login with bad password")
    public void badLoginPassword() throws TestException {
        try {
            // Bad password
            assertThrows(
                    ServiceException.class,
                    () -> userService.login(new LoginRequest("pyford", "i_hate_cs240")),
                    "Accepted bad password when it shouldn't have"
            );
        } catch (Exception ex) {
            throw new TestException("Could not login");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Logout")
    public void logout() throws TestException {
        try {
            LoginResponse loginResponse = userService.login(new LoginRequest("pyford", "i_love_cs240"));
            userService.logout(loginResponse.authToken());
        } catch (Exception ex) {
            throw new TestException("Could not logout");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Logout with bad authToken")
    public void badLogout() throws TestException {
        try {
            LoginResponse loginResponse = userService.login(new LoginRequest("pyford", "i_love_cs240"));
            userService.logout(loginResponse.authToken());
            assertThrows(
                    ServiceException.class,
                    () -> userService.logout(loginResponse.authToken()),
                    "authToken is still valid after logout when it shouldn't be"
            );
        } catch (Exception ex) {
            throw new TestException("Could not logout");
        }
    }
}
