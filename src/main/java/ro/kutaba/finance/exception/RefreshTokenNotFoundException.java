package ro.kutaba.finance.exception;

public class RefreshTokenNotFoundException extends RuntimeException {

    public RefreshTokenNotFoundException(){
        super("Refresh token not found");
    }
    
}
