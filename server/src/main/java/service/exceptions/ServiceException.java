package service.exceptions;

public class ServiceException extends Exception {
    protected int code = 500;
    public ServiceException(String message) {
        super(message);
    }
    // getCode must be overridden in the derived classes because in Java, member variables are not polymorphic although methods are
    public int getCode() {
        return this.code;
    }

}
