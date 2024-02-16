package service;

import chess.dataModel.AuthData;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import service.exceptions.ServiceException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Service {
    // Derived classes are all singletons as they share the database.
    protected static MemoryDataAccess db = new MemoryDataAccess();

    protected static void verifyAuthToken(String authToken) throws ServiceException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
    }

    /**
     * Throws a ServiceException if there exists a field of the given object that is either null or its default value.
     *
     * @param request The request object for fields checking.
     * @param ignore  A collection of field names to ignore when checking.
     */
    protected static void verifyRequestFields(Object request, Collection<String> ignore) throws ServiceException {
        Class<?> clazz = request.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (ignore.contains(field.getName())) {
                    continue;
                }
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

    /**
     * Throws a ServiceException if there exists a field of the given object that is either null or its default value.
     *
     * @param request The request object for fields checking.
     */
    protected static void verifyRequestFields(Object request) throws ServiceException {
        verifyRequestFields(request, new ArrayList<>());
    }
}
