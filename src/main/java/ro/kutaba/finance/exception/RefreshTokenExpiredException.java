package ro.kutaba.finance.exception;

public class RefreshTokenExpiredException extends RuntimeException {

    public RefreshTokenExpiredException() {
        super("Refresh token expired");
    }
}