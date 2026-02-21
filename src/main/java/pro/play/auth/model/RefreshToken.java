package pro.play.auth.model;

import jakarta.persistence.*;
import lombok.*;
import pro.play.audit.AuditModel;
import pro.play.user.model.User;

import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "revoked")
    private boolean revoked;
}

