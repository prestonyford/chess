package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;

public abstract class DataAccess {
    public abstract UserData getUser(String username) throws DataAccessException;
    public abstract void createUser(UserData userData) throws DataAccessException;
    public abstract void insertAuth(AuthData authData) throws DataAccessException;
    public abstract AuthData getAuth(String authToken) throws DataAccessException;
    public abstract void deleteAuth(AuthData authData) throws DataAccessException;
    public abstract Collection<GameData> listGames() throws DataAccessException;
    public abstract void insertGame(GameData gameData) throws DataAccessException;
    public abstract GameData getGame(int gameID) throws DataAccessException;
    public abstract void updateGame(int gameID, GameData gameData) throws DataAccessException;
    public abstract void clear();
}
