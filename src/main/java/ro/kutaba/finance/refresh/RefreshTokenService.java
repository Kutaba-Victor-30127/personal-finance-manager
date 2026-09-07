package ro.kutaba.finance.refresh;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.kutaba.finance.config.JwtService;
import ro.kutaba.finance.exception.RefreshTokenExpiredException;
import ro.kutaba.finance.exception.RefreshTokenNotFoundException;
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

    @Transactional
    public RefreshToken createRefreshToken(User user){

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush(); 

        String token = jwtService.generateRefreshToken(user.getUsername());

        RefreshToken refreshToken = new RefreshToken(
            token,
            LocalDateTime.now().plusDays(7),
            user
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token){

        return refreshTokenRepository.findByToken(token)
                            .orElseThrow(RefreshTokenNotFoundException::new);
    }

    public RefreshToken findByTokenForUpdate(String token) {

        return refreshTokenRepository.findByTokenForUpdate(token)
                            .orElseThrow(RefreshTokenNotFoundException::new);
    }

    public RefreshToken verifyExpiration(RefreshToken token){

        if (token.getExpiryDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(token);

            throw new RefreshTokenExpiredException();
        }
        return token;
    }

    public void deleteByUser(User user){

        refreshTokenRepository.deleteByUser(user);
    }

    public void delete(RefreshToken refreshToken){

        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.flush();
    }


}
