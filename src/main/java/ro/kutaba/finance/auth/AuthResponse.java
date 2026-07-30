package ro.kutaba.finance.auth;

public record AuthResponse (
    String accessToken,
    String refreshToken
){
}

