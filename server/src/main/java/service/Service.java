package service;

import chess.dataModel.AuthData;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import service.exceptions.ServiceException;

import java.lang.reflect.Field;

public abstract class Service {
    protected DataAccess db;

    public Service(DataAccess db) {
        this.db = db;
    }

    /**
     * Throws a ServiceException if the auth token is not valid in the database.
     *
     * @param authToken The auth token for verifying.
     */
    protected void verifyAuthToken(String authToken) throws ServiceException, DataAccessException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
    }

    /**
     * Throws a ServiceException if there exists a field of the given object that is an empty/null string or an int that is 0
     *
     * @param request The request object for fields checking.
     */
    protected static void verifyRequestFields(Object request) throws ServiceException {
        Class<?> clazz = request.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(request);
                // Check if the field is its default value; null for strings, 0 for ints
                if (
                        value == null ||
                                String.valueOf(value).isEmpty() ||
                                (value instanceof Integer && (Integer) value == 0)
                ) {
                    throw new ServiceException(400, "Error: bad request");
                }
            }
        } catch (IllegalAccessException ex) {
            throw new ServiceException(500, "I did an oopsie daisy");
        }
    }
}
