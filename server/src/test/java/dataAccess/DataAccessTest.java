package dataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.exceptions.ServiceException;

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
    public void clearDB(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);
        dataAccess.clear();
    }
}
