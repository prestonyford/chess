package service;

import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.RegisterResponse;

public class UserService extends Service {
    public RegisterResponse register(RegisterRequest registerRequest) throws ResponseException {
        if (db.getUser(registerRequest.username()) != null) {
            throw new ResponseException("Error: already taken");
        }
    }
}
