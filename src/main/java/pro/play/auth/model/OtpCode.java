package pro.play.auth.model;

import jakarta.persistence.*;
import lombok.*;
import pro.play.audit.AuditModel;

import java.time.OffsetDateTime;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "attempts_left")
    private Integer attemptsLeft;

    @Column(name = "used")
    private boolean used;
}

