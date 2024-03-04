import chess.ChessGame;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.SQLDataAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws DataAccessException {
        DataAccess db;
        if (databaseClass.equals(SQLDataAccess.class)) {
            db = new SQLDataAccess();
        } else {
            db = new MemoryDataAccess();
        }
        db.clear();
        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void getUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        dataAccess.createUser(new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));
        assertEquals(dataAccess.getUser("Navia"), new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badGetUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        dataAccess.createUser(new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));
        assertEquals(dataAccess.getUser("Navia"), new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void createUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        dataAccess.createUser(new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));

        assertEquals(dataAccess.getUser("Navia"), new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badCreateUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        dataAccess.createUser(new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));
        assertThrows(
                DataAccessException.class,
                () -> dataAccess.createUser(
                        new UserData(
                                "Navia",
                                "Navia's password",
                                "Navia@email.com"
                        )
                ),
                "Expected DataAccessException when creating duplicate user"
        );
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void insertAuth(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        dataAccess.insertAuth(new AuthData(
                "token0",
                "Navia"
        ));
        assertEquals(dataAccess.getAuth("token0"), new AuthData(
                "token0",
                "Navia"
        ));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badInsertAuth(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        assertThrows(
                DataAccessException.class,
                () -> dataAccess.insertAuth(new AuthData(
                        null,
                        "Navia"
                )),
                "database allowed a null authToken when it shouldn't have"
        );
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void getAuth(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        dataAccess.insertAuth(new AuthData(
                "token0",
                "Navia"
        ));
        assertEquals(dataAccess.getAuth("token0"), new AuthData(
                "token0",
                "Navia"
        ));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badGetAuth(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        assertNull(dataAccess.getAuth("token0"), "database returned an authdata when it shouldn't have");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void deleteAuth(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.insertAuth(new AuthData(
                "token0",
                "Navia"
        ));
        assertNotNull(dataAccess.getAuth("token0"));

        dataAccess.deleteAuth(new AuthData(
                "token0",
                "Navia"
        ));
        assertNull(dataAccess.getAuth("token0"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badDeleteAuth(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.insertAuth(new AuthData(
                "token0",
                "Navia"
        ));
        assertNotNull(dataAccess.getAuth("token0"));

        dataAccess.deleteAuth(new AuthData(
                "token1",
                "Navia"
        ));
        assertNotNull(dataAccess.getAuth("token0"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void listGames(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        var game1 = dataAccess.insertGame(new GameData(
                null,
                "p1",
                "p2",
                "game1",
                new ChessGame()
        ));
        var game2 = dataAccess.insertGame(new GameData(
                null,
                "p1",
                "p2",
                "game2",
                new ChessGame()
        ));
        var game3 = dataAccess.insertGame(new GameData(
                null,
                "p1",
                "p2",
                "game3",
                new ChessGame()
        ));

        dataAccess.insertGame(game1);
        dataAccess.insertGame(game2);
        dataAccess.insertGame(game3);

        var games = dataAccess.listGames();
        assertTrue(games.containsAll(List.of(game1, game2, game3)));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badListGames(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        var game1 = dataAccess.insertGame(new GameData(
                null,
                "p1",
                "p2",
                "sameName",
                new ChessGame()
        ));
        var game2 = dataAccess.insertGame(new GameData(
                null,
                "p1",
                "p2",
                "sameName",
                new ChessGame()
        ));

        dataAccess.insertGame(game1);
        dataAccess.insertGame(game2);

        var games = dataAccess.listGames();
        assertTrue(games.containsAll(List.of(game1, game2)));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void insertGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game = dataAccess.insertGame(new GameData(
                null,
                "p1",
                "p2",
                "game1",
                new ChessGame()
        ));
        assertNotNull(game.gameID(), "gameID was still null after inserting to the database when it shouldn't have been");
        assertEquals(game, dataAccess.getGame(game.gameID()), "could not find game with gameID, or one was found but was not equal");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    @DisplayName("Attempting to add a game with existing ID")
    public void badInsertGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game1 = dataAccess.insertGame(new GameData(
                null,
                "p1",
                "p2",
                "game1",
                new ChessGame()
        ));
        var game2 = dataAccess.insertGame(new GameData(
                game1.gameID(),
                "p1",
                "p2",
                "game2",
                new ChessGame()
        ));
        assertEquals(new GameData(
                game1.gameID(),
                "p1",
                "p2",
                "game1",
                new ChessGame()
        ), game1);
        assertNotEquals(game1.gameID(), game2.gameID());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void getGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game = dataAccess.insertGame(new GameData(
                null,
                "pl1",
                "pl2",
                "game1",
                new ChessGame()
        ));
        assertNotNull(game.gameID());
        assertEquals(game, dataAccess.getGame(game.gameID()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badGetGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game = dataAccess.insertGame(new GameData(
                null,
                "pl1",
                "pl2",
                "game1",
                new ChessGame()
        ));
        assertNotNull(game.gameID());
        assertNull(dataAccess.getGame(0));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void updateGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game = dataAccess.insertGame(new GameData(
                null,
                null,
                null,
                "game1",
                new ChessGame()
        ));

        dataAccess.updateGame(
                game.gameID(),
                new GameData(
                        null,
                        "player1",
                        null,
                        "game1",
                        new ChessGame()
                )
        );

        assertEquals(
                new GameData(
                        game.gameID(),
                        "player1",
                        null,
                        "game1",
                        new ChessGame()
                ),
                dataAccess.getGame(game.gameID()),
                "Game was not updated correctly"
        );
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void badUpdateGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game = dataAccess.insertGame(new GameData(
                null,
                null,
                null,
                "game1",
                new ChessGame()
        ));

        dataAccess.updateGame(
                -1,
                new GameData(
                        null,
                        "player1",
                        null,
                        "game1",
                        new ChessGame()
                )
        );

        assertEquals(
                new GameData(
                        game.gameID(),
                        null,
                        null,
                        "game1",
                        new ChessGame()
                ),
                game
        );
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, SQLDataAccess.class})
    public void clear(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        // Create a user
        dataAccess.createUser(new UserData(
                "Navia",
                "Navia's password",
                "Navia@email.com"
        ));
        assertNotNull(dataAccess.getUser("Navia"), "Could not add a user to the db");
        dataAccess.clear();
        assertNull(dataAccess.getUser("Navia"), "User was still in db after clear");
    }
}
