package dataAccess;

import org.junit.jupiter.api.BeforeEach;
import service.exceptions.ServiceException;

public class DataAccessTest {
    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws ServiceException, DataAccessException {
        DataAccess db;
        if (databaseClass.equals(SQLDataAccess.class)) {
            db = new SQLDataAccess();
        } else {
            db = new MemoryDataAccess();
        }
        db.clear();
        return db;
    }

/*    @BeforeEach
    public void clearDB() {
        getDataAccess()
    }*/
}
