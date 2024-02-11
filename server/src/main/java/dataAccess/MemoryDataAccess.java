package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class MemoryDataAccess extends DataAccess {
    private HashSet<UserData> users = new HashSet<>();
    private HashSet<AuthData> auths = new HashSet<>();
    private HashSet<GameData> games = new HashSet<>();

    @Override
    public UserData getUser(String username) {
        for (var user: users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) {
        users.add(userData);
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.add(authData);
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
