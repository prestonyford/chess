package dataAccess;

import chess.ChessGame;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;
import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccess implements DataAccess {

    private void initializeDatabase() throws DataAccessException {
        String initUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    username varchar(256) NOT NULL,
                    password varchar(256) NOT NULL,
                    email varchar(256) NOT NULL,
                    PRIMARY KEY (username)
                );
                """;
        String initAuthsTable = """
                CREATE TABLE IF NOT EXISTS auths (
                    username varchar(256) NOT NULL,
                    authToken varchar(256) NOT NULL,
                    PRIMARY KEY (authToken)
                );
                """;
        String initGamesTable = """
                CREATE TABLE IF NOT EXISTS games (
                    gameID int NOT NULL AUTO_INCREMENT,
                    gameName varchar(256) NOT NULL,
                    whiteUsername varchar(256),
                    blackUsername varchar(256),
                    game longtext NOT NULL,
                    concluded boolean DEFAULT FALSE,
                    PRIMARY KEY (gameID)
                );
                """;

        String[] createStatements = new String[]{
                initUsersTable,
                initAuthsTable,
                initGamesTable
        };

        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public SQLDataAccess() throws DataAccessException {
        initializeDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users WHERE username=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?);";
        executeUpdate(statement, authData.username(), authData.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, authToken FROM auths WHERE authToken=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username")
                        );
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE authToken=?;";
        executeUpdate(statement, authData.authToken());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                new Gson().fromJson(rs.getString("game"), ChessGame.class),
                                false));
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return games;
    }

    @Override
    public GameData insertGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?);";
        int id = executeUpdate(statement, gameData.gameName(), gameData.whiteUsername(), gameData.blackUsername(), gameData.game());

        return new GameData(
                id,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game(),
                false);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, gameName, whiteUsername, blackUsername, game, concluded FROM games WHERE gameID=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                new Gson().fromJson(rs.getString("game"), ChessGame.class),
                                rs.getBoolean("concluded"));
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=?, concluded=? WHERE gameID=?;";
        executeUpdate(
                statement,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                new Gson().toJson(gameData.game()),
                gameData.concluded(),
                gameID
        );
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE users;");
        executeUpdate("TRUNCATE auths;");
        executeUpdate("TRUNCATE games;");
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, new Gson().toJson(p));
                    else if (param instanceof Boolean p) ps.setBoolean(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
