package service.exceptions;

public class InternalServerErrorException extends ServiceException {
    public InternalServerErrorException(String message) {
        super(message);
    }

    @Override
    public int getCode() {
        return 500;
    }
}
