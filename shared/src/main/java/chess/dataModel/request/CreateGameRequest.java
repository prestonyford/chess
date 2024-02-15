package chess.dataModel.request;

import chess.dataModel.IAuthorized;

public class CreateGameRequest implements IAuthorized {
    private String authToken;
    private String gameName;
    public CreateGameRequest(String authToken, String gameName){
        this.authToken = authToken;
        this.gameName = gameName;
    }
    @Override
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    @Override
    public String getAuthToken() {
        return this.authToken;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    public String getGameName() {
        return this.gameName;
    }
}
