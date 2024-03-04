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

    public MemoryDataAccess() throws DataAccessException {

    }

    private static class IDGen {
        private static int latestID = 1;

        public static int newID() {
            return latestID++;
        }
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
    public void createUser(UserData userData) throws DataAccessException {
        if (users.contains(userData)) {
            throw new DataAccessException("Existing user");
        }
        users.add(userData);
    }

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        // Add new auth
        if (authData.authToken() == null) {
            throw new DataAccessException("Null authToken");
        }
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
    public GameData insertGame(GameData gameData) {
        gameData = new GameData(
                IDGen.newID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        );
        games.add(gameData);
        return gameData;
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
        games.add(new GameData(
                gameID,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        ));
    }

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
