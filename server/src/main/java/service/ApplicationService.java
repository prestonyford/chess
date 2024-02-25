package service;

import dataAccess.DataAccess;

public class ApplicationService extends Service {
    public ApplicationService(DataAccess db) {
        super(db);
    }

    public void bigRedButton() {
        db.clear();
    }
}
