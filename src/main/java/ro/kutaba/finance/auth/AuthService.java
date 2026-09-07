package ro.kutaba.finance.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.kutaba.finance.user.UserRepository;
import ro.kutaba.finance.user.Role;
import ro.kutaba.finance.user.User;
import ro.kutaba.finance.config.JwtService;
import ro.kutaba.finance.exception.UserNotFoundException;
import ro.kutaba.finance.refresh.RefreshToken;
import ro.kutaba.finance.refresh.RefreshTokenService;
import ro.kutaba.finance.exception.InvalidCredentialsException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
                    UserRepository userRepository, 
                    PasswordEncoder passwordEncoder, 
                    JwtService jwtService,
                    RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegisterRequest request){

        if (userRepository.findByUsername(request.username()).isPresent()){
            throw new UserNotFoundException();
        }

        User user = new User(
            request.username(),
            passwordEncoder.encode(request.password()),
            Role.USER
        );

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(
            request.password(),
            user.getPassword()
        )){
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
            accessToken,
            refreshToken.getToken()
        );
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request){

        RefreshToken refreshToken =
                refreshTokenService.findByTokenForUpdate(request.refreshToken());

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        refreshTokenService.delete(refreshToken);
        
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        String accessToken = jwtService.generateAccessToken(user.getUsername());

        return new AuthResponse(
            accessToken,
            newRefreshToken.getToken()
        );
        
    }

    public void logout(RefreshTokenRequest request){

        RefreshToken refreshToken = 
                refreshTokenService.findByToken(
                    request.refreshToken()
                );

        refreshTokenService.delete(refreshToken);
    }

}
