package pro.play.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtUtil(
            @Value("${app.jwt.secret:change-me-please}") String secret,
            @Value("${app.jwt.access-ms:900000}") long accessMs, // 15 minutes default
            @Value("${app.jwt.refresh-ms:604800000}") long refreshMs // 7 days default
    ) {
        // Ensure the secret material is 256-bit (32 bytes) for HS256 signing. If the provided
        // secret is too short, derive a 32-byte key using SHA-256 of the secret.
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                // Fallback: pad or truncate to 32 bytes
                keyBytes = Arrays.copyOf(keyBytes, 32);
            }
        } else if (keyBytes.length > 32) {
            // If longer, use SHA-256 to normalize length for consistency
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                keyBytes = Arrays.copyOf(keyBytes, 32);
            }
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityMs = accessMs;
        this.refreshTokenValidityMs = refreshMs;
    }

    public String generateAccessToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityMs);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("typ", "access")
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityMs);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("typ", "refresh")
                .signWith(key)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims c = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Object typ = c.get("typ");
            return typ != null && "refresh".equals(typ.toString());
        } catch (Exception e) {
            return false;
        }
    }

    public Date getExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
    }
}
