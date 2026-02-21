package pro.play.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}

