package service.exceptions;

public abstract class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }
    // getCode must be overridden in the derived classes because in Java, member variables are not polymorphic although methods are
    public abstract int getCode();

}
