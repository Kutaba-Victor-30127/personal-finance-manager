package ro.kutaba.finance.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
        @Valid 
        @RequestBody RegisterRequest request){
        
        authService.register(request);

        return ResponseEntity.ok(
            new MessageResponse("User registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @Valid
        @RequestBody LoginRequest request){

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
        @RequestBody RefreshTokenRequest request){

        AuthResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(response);
    }
    
}
