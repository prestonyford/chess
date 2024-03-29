package service;

import chess.dataModel.AuthData;
import chess.dataModel.UserData;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.exceptions.ServiceException;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public class UserService extends Service {
    public UserService(DataAccess db) {
        super(db);
    }

    private static class AuthTokenGen {
        private static final SecureRandom secureRandom = new SecureRandom();
        private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

        public static String createAuthToken() {
            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            return base64Encoder.encodeToString(randomBytes);
        }
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws ServiceException, DataAccessException {
        verifyRequestFields(registerRequest);
        var user = db.getUser(registerRequest.username());
        if (user != null) {
            throw new ServiceException(403, "Error: already taken");
        }

        // Hash password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        var hashPass = encoder.encode(registerRequest.password());

        db.createUser(new UserData(registerRequest.username(), hashPass, registerRequest.email()));
        var authData = new AuthData(AuthTokenGen.createAuthToken(), registerRequest.username());
        db.insertAuth(authData);
        return new RegisterResponse(authData.username(), authData.authToken());
    }

    public LoginResponse login(LoginRequest loginRequest) throws ServiceException, DataAccessException {
        UserData user = db.getUser(loginRequest.username());
        if (user == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        var hashPass = user.password();

        if (!encoder.matches(loginRequest.password(), hashPass)) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        // Create new auth
        var authData = new AuthData(AuthTokenGen.createAuthToken(), loginRequest.username());
        db.insertAuth(authData);
        return new LoginResponse(authData.username(), authData.authToken());
    }

    public void logout(String authToken) throws ServiceException, DataAccessException {
        verifyAuthToken(authToken);
        AuthData authData = db.getAuth(authToken);
        db.deleteAuth(authData);
    }
}
