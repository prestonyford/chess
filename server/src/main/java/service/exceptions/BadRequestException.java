package service.exceptions;

public class BadRequestException extends ServiceException {
    public BadRequestException(String message) {
        super(message);
    }
    @Override
    public int getCode() {
        return 400;
    }
}
