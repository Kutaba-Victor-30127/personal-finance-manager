package ro.kutaba.finance.refresh;

import org.springframework.stereotype.Service;

import ro.kutaba.finance.config.JwtService;

@Service
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService){
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }




}
