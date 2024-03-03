package dataAccess;

import chess.ChessGame;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTest {
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
    public void insertGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.insertGame(new GameData(
                1,
                "p1",
                "p2",
                "game1",
                new ChessGame()
        ));
        
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
