package service.exceptions;

public class UserAlreadyTakenException extends ServiceException {
    protected int code = 403;
    public UserAlreadyTakenException(String message) {
        super(message);
    }
    @Override
    public int getCode() {
        return this.code;
    }
}
