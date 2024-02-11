package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;

public class SQLDataAccess extends DataAccess {
    @Override
    public UserData getUser(String username) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void createUser(UserData userData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void createAuth(AuthData authData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAuth(String authToken) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<GameData> listGames() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public GameData createGame(String gameName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public GameData updateGame(GameData gameData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Not implemented");
    }
}
