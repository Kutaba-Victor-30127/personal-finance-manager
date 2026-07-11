package ro.kutaba.finance.auth;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 3, message = "Password must contain at least 3 characters")
    String password
) {
} 
    
