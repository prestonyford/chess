package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;
import service.exceptions.ServiceException;

import java.util.Collection;

public interface DataAccess {
    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    void insertAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(AuthData authData) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData insertGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, GameData gameData) throws DataAccessException;

    void clear() throws DataAccessException;
}
