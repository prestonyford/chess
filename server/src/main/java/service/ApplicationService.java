package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class ApplicationService extends Service {
    public ApplicationService(DataAccess db) {
        super(db);
    }

    public void bigRedButton() throws DataAccessException {
        db.clear();
    }
}
