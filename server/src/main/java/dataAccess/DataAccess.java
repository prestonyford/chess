package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;

public abstract class DataAccess {
    public abstract UserData getUser(String username) throws DataAccessException;
    public abstract void createUser(UserData userData) throws DataAccessException;
    public abstract void createAuth(AuthData authData) throws DataAccessException;
    public abstract void deleteAuth(String authToken) throws DataAccessException;
    public abstract Collection<GameData> listGames() throws DataAccessException;
    public abstract GameData createGame(String gameName) throws DataAccessException;
    public abstract GameData updateGame(GameData gameData) throws DataAccessException;
    public abstract void clear();
}
