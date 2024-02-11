package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryDataAccess extends DataAccess {
    private HashSet<UserData> users = new HashSet<>();
    private HashSet<AuthData> auths = new HashSet<>();
    private HashSet<GameData> games = new HashSet<>();

    @Override
    public UserData getUser(String username) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void createUser(UserData userData) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AuthData createAuth(String username) {
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
        users.clear();
        auths.clear();
        games.clear();
    }
}
