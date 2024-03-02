package dataAccess;

import chess.dataModel.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
