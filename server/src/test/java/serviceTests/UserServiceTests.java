package serviceTests;

import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import service.UserService;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private final UserService userService = UserService.getInstance();

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
            throw new TestException("Could not register");
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
}
