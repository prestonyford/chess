package service.exceptions;

public class ServiceException extends Exception {
    private final int code;
    public ServiceException(int statusCode, String message) {
        super(message);
        this.code = statusCode;
    }
    // getCode must be overridden in the derived classes because in Java, member variables are not polymorphic although methods are
    public int getCode() {
        return code;
    }
}
