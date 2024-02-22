package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess {
    private final HashSet<UserData> users = new HashSet<>();
    private final HashSet<AuthData> auths = new HashSet<>();
    private final HashSet<GameData> games = new HashSet<>();

    private static final MemoryDataAccess INSTANCE = new MemoryDataAccess();

    private MemoryDataAccess() {
    }

    public static MemoryDataAccess getInstance() {
        return INSTANCE;
    }

    @Override
    public UserData getUser(String username) {
        for (var user : users) {
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
    public void insertAuth(AuthData authData) {
        // Add new auth
        auths.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (var auth : auths) {
            if (Objects.equals(auth.authToken(), authToken)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        auths.remove(authData);
    }

    @Override
    public Collection<GameData> listGames() {
        return games;
    }

    @Override
    public void insertGame(GameData gameData) {
        games.add(gameData);
    }

    @Override
    public GameData getGame(int gameID) {
        for (var game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {
        for (var game : games) {
            if (game.gameID() == gameID) {
                games.remove(game);
                break;
            }
        }
        games.add(gameData);
    }

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
