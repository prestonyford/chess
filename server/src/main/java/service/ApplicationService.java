package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;

public class ApplicationService {
    DataAccess db = new MemoryDataAccess();
    public void clearDatabase() {
        db.clear();
    }
}
