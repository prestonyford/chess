package chess.dataModel.request;

import chess.dataModel.IAuthorized;

public class LogoutRequest implements IAuthorized {
    private final String authToken;
    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }
    @Override
    public String getAuthToken() {
        return authToken;
    }
}
