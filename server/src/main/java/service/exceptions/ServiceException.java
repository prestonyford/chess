package service.exceptions;

public class ServiceException extends Exception {
    private final int code;

    public ServiceException(int statusCode, String message) {
        super(message);
        this.code = statusCode;
    }

    public int getCode() {
        return code;
    }
}
