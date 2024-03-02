package dataAccess;

import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;
import service.exceptions.ServiceException;

import java.sql.SQLException;
import java.util.Collection;

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
        } catch (DataAccessException ex) {
            System.out.println("oopsie");
        } catch (ServiceException ex) {
            System.out.println(ex.getMessage());
        }
    }

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
