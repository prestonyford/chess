package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;

public abstract class Service {
    protected static MemoryDataAccess db = new MemoryDataAccess();
}
