package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;
import service.exceptions.ServiceException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccess implements DataAccess {

    private void initializeDatabase() throws DataAccessException, ServiceException {
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
                    PRIMARY KEY (username)
                );
                """;
        String initGamesTable = """
                CREATE TABLE IF NOT EXISTS games (
                    gameID int NOT NULL AUTO_INCREMENT,
                    gameName varchar(256) NOT NULL,
                    whiteUsername varchar(256) NOT NULL,
                    blackUsername varchar(256) NOT NULL,
                    game longtext NOT NULL,
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
            throw new ServiceException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public SQLDataAccess() {
        try {
            initializeDatabase();
        } catch (DataAccessException | ServiceException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users WHERE username=?";
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
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
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
                        // else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
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
