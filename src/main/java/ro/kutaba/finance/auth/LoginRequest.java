package ro.kutaba.finance.auth;

public record LoginRequest(
    String username,
    String password
) {
}
