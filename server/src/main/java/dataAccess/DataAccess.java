package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;

public abstract class DataAccess {
    public abstract UserData getUser(String username);
    public abstract void createUser(UserData userData) throws DataAccessException;
    public abstract AuthData createAuth(String username) throws DataAccessException;
    public abstract void deleteAuth(String authToken) throws DataAccessException;
    public abstract Collection<GameData> listGames();
    public abstract GameData createGame(String gameName);
    public abstract GameData updateGame(GameData gameData) throws DataAccessException;
    public abstract void clear();
}
