package service.exceptions;

public class UserAlreadyTakenException extends ServiceException {
    public UserAlreadyTakenException(String message) {
        super(message);
    }
    @Override
    public int getCode() {
        return 403;
    }
}
