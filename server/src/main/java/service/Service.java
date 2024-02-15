package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;

public abstract class Service {
    // Derived classes are all singletons as they share the database.
    protected static MemoryDataAccess db = new MemoryDataAccess();
}
