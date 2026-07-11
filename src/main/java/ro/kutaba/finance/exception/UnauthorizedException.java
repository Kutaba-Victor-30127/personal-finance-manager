package ro.kutaba.finance.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("You are not allowed to access this resource");
    }
}