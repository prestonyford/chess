package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;

public abstract class Service {
    DataAccess db = new MemoryDataAccess();
}
