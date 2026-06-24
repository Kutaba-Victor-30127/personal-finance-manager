package ro.kutaba.finance.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.kutaba.finance.user.UserRepository;
import ro.kutaba.finance.user.Role;
import ro.kutaba.finance.user.User;
import ro.kutaba.finance.config.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = new JwtService();
    }

    public void register(RegisterRequest request){

        if (userRepository.findByUsername(request.username()).isPresent()){
            throw new RuntimeException("Username already exists");
        }

        User user = new User(
            request.username(),
            passwordEncoder.encode(request.password()),
            Role.USER
        );

        userRepository.save(user);
    }

    public String login(LoginRequest request){
        
        User user = userRepository.findByUsername(request.username())
        .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
            request.password(),
            user.getPassword()
        )){
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user.getUsername());
    }

}
