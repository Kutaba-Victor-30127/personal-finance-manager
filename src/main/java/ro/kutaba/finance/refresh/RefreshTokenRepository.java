package ro.kutaba.finance.refresh;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.kutaba.finance.user.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);

}
