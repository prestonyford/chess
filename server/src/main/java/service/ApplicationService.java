package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;

public class ApplicationService extends Service {
    public void clearDatabase() {
        db.clear();
    }
}
