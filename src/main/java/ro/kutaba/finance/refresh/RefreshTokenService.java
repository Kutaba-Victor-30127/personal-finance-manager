package ro.kutaba.finance.refresh;

import org.springframework.stereotype.Service;

import ro.kutaba.finance.config.JwtService;
import ro.kutaba.finance.user.User;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService){
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(User user){

        refreshTokenRepository.deleteByUser(user);

        String token = jwtService.generateRefreshToken(user.getUsername());

        RefreshToken refreshToken = new RefreshToken(
            token,
            LocalDateTime.now().plusDays(7),
            user
        );

        return refreshTokenRepository.save(refreshToken);
    }

    


}
