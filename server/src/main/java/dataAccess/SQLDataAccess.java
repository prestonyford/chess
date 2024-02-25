package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;

public class SQLDataAccess implements DataAccess {

    @Override
    public UserData getUser(String username) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void createUser(UserData userData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void insertAuth(AuthData authData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AuthData getAuth(String authToken) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAuth(AuthData authData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<GameData> listGames() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void insertGame(GameData gameData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Not implemented");
    }
}
