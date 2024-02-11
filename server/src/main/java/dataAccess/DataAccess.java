package dataAccess;

import chess.AuthData;
import chess.GameData;
import chess.UserData;

import java.util.Collection;

public abstract class DataAccess {
    public abstract UserData getUser(String username);
    public abstract void createUser(UserData userData);
    public abstract AuthData createAuth(String username);
    public abstract void deleteAuth(String authToken);
    public abstract Collection<GameData> listGames();
    public abstract GameData createGame(String gameName);
    public abstract GameData updateGame(GameData gameData);
    public abstract void clear();
}
