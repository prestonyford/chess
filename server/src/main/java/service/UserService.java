package service;

import chess.dataModel.AuthData;
import chess.dataModel.UserData;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.RegisterResponse;
import dataAccess.DataAccessException;

import java.security.SecureRandom;
import java.util.Base64;

public class UserService extends Service {
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private String createAuthToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException {
        if (db.getUser(registerRequest.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }
        db.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
        var authData = new AuthData(createAuthToken(), registerRequest.username());
        db.createAuth(authData);
        return new RegisterResponse(authData.username(), authData.authToken());
    }
}
