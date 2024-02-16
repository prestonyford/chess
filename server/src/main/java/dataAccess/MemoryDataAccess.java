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
        // Remove old auth
        /*for (var auth: auths) {
            if (Objects.equals(auth.username(), authData.username())){
                deleteAuth(auth);
                System.out.println("deleted old auth");
                break;
            }
        }*/
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
    public GameData getGame(int gameID) throws DataAccessException {
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
