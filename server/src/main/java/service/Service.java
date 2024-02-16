package service;

import chess.dataModel.AuthData;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import service.exceptions.ServiceException;

import java.lang.reflect.Field;

public abstract class Service {
    // Derived classes are all singletons as they share the database.
    protected static MemoryDataAccess db = new MemoryDataAccess();

    protected static void verifyAuthToken(String authToken) throws ServiceException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
    }

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
                                String.valueOf(request).isEmpty() ||
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
