package chess.dataModel.request;

import chess.dataModel.IAuthorized;

public class LogoutRequest implements IAuthorized {
    private String authToken;
    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }
    @Override
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    @Override
    public String getAuthToken() {
        return authToken;
    }
}
