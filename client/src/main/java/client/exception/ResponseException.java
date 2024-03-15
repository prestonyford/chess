package client.exception;

public class ResponseException extends Exception {
    private final int code;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.code = statusCode;
    }

    public int getCode() {
        return code;
    }
}
