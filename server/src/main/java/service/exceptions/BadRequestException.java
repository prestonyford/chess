package service.exceptions;

public class BadRequestException extends ServiceException {
    protected int code = 400;
    public BadRequestException(String message) {
        super(message);
    }
    @Override
    public int getCode() {
        return this.code;
    }
}
