package pro.play.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.auth.model.OtpCode;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByMobileNumberOrderByIdDesc(String mobileNumber);
}

