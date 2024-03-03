package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import service.exceptions.ServiceException;

public class ApplicationService extends Service {
    public ApplicationService(DataAccess db) {
        super(db);
    }

    public void bigRedButton() throws DataAccessException {
        db.clear();
    }
}
