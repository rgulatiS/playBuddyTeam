package pro.play.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.play.auth.dto.*;
import pro.play.auth.model.OtpCode;
import pro.play.auth.model.RefreshToken;
import pro.play.auth.repository.OtpCodeRepository;
import pro.play.auth.repository.RefreshTokenRepository;
import pro.play.auth.service.AuthService;
import pro.play.security.JwtUtil;
import pro.play.user.model.Role;
import pro.play.user.model.User;
import pro.play.user.repository.UserRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final Random random = new Random();
    private final int otpLength = 6;
    // OTP settings are configurable via application properties
    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Value("${app.otp.max-attempts:5}")
    private int otpMaxAttempts;

    @Override
    @Transactional
    public TokenResponse registerByEmail(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = User.builder()
                .email(req.getEmail())
                .name(req.getName())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .mobileNumber(null)
                .build();
        userRepository.save(u);
        String access = jwtUtil.generateAccessToken(u.getEmail());
        String refresh = jwtUtil.generateRefreshToken(u.getEmail());
        storeRefreshToken(u, refresh);
        return new TokenResponse(access, refresh, u.getId(), u.getName(), u.getEmail());
    }

    @Override
    @Transactional
    public OtpSentResponse registerByMobile(MobileRegisterRequest req) {
        // Send OTP to email for registration (dual identity collected)
        String code = generateOtp();
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(otpExpiryMinutes);
        OtpCode otp = OtpCode.builder()
                .email(req.getEmail())
                .mobileNumber(req.getMobileNumber())
                .code(code)
                .expiresAt(expiresAt)
                .attemptsLeft(otpMaxAttempts)
                .used(false)
                .build();
        otpCodeRepository.save(otp);
        // In development, we return the code for verification.
        // In production, send Email here.
        return new OtpSentResponse(true, code);
    }

    @Override
    @Transactional
    public TokenResponse verifyOtp(VerifyOtpRequest req) {
        Optional<OtpCode> maybe = otpCodeRepository.findTopByEmailOrderByIdDesc(req.getEmail());
        if (maybe.isEmpty())
            throw new IllegalArgumentException("No OTP sent to this email");
        OtpCode otp = maybe.get();
        if (otp.isUsed())
            throw new IllegalArgumentException("OTP already used");
        if (otp.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC)))
            throw new IllegalArgumentException("OTP expired");
        if (otp.getAttemptsLeft() == null || otp.getAttemptsLeft() <= 0)
            throw new IllegalArgumentException("No attempts left");
        if (!otp.getCode().equals(req.getCode())) {
            otp.setAttemptsLeft(otp.getAttemptsLeft() - 1);
            otpCodeRepository.save(otp);
            throw new IllegalArgumentException("Invalid OTP");
        }
        otp.setUsed(true);
        otpCodeRepository.save(otp);

        // Find or create user, and ensure profile is complete/updated
        User user = userRepository.findByEmail(req.getEmail())
                .orElseGet(() -> User.builder().email(req.getEmail()).role(Role.USER).build());

        // For new users, name and password are required
        boolean isNewUser = user.getId() == null;

        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            user.setName(req.getName());
        } else if (isNewUser) {
            throw new IllegalArgumentException("Name is required for registration");
        }

        if (req.getMobileNumber() != null && !req.getMobileNumber().trim().isEmpty()) {
            user.setMobileNumber(req.getMobileNumber());
        } else if (user.getMobileNumber() == null) {
            user.setMobileNumber(otp.getMobileNumber());
        }

        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        } else if (isNewUser || user.getPasswordHash() == null) {
            throw new IllegalArgumentException("Password is required for registration");
        }

        userRepository.save(user);

        String access = jwtUtil.generateAccessToken(user.getEmail());
        String refresh = jwtUtil.generateRefreshToken(user.getEmail());
        storeRefreshToken(user, refresh);
        return new TokenResponse(access, refresh, user.getId(), user.getName(), user.getEmail());
    }

    @Override
    @Transactional
    public TokenResponse loginByEmail(AuthRequest req) {
        // authenticate
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        User user = userRepository.findByEmail(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        String access = jwtUtil.generateAccessToken(user.getEmail());
        String refresh = jwtUtil.generateRefreshToken(user.getEmail());
        storeRefreshToken(user, refresh);
        return new TokenResponse(access, refresh, user.getId(), user.getName(), user.getEmail());
    }

    @Override
    @Transactional
    public OtpSentResponse loginByMobile(MobileRegisterRequest req) {
        // For login via mobile, send OTP (user must exist)
        if (userRepository.findByMobileNumber(req.getMobileNumber()).isEmpty()) {
            throw new IllegalArgumentException("Mobile number not registered");
        }
        String code = generateOtp();
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(otpExpiryMinutes);
        OtpCode otp = OtpCode.builder()
                .mobileNumber(req.getMobileNumber())
                .code(code)
                .expiresAt(expiresAt)
                .attemptsLeft(otpMaxAttempts)
                .used(false)
                .build();
        otpCodeRepository.save(otp);
        return new OtpSentResponse(true, code);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshRequest req) {
        String token = req.getRefreshToken();
        if (!jwtUtil.isRefreshToken(token) || !jwtUtil.validate(token)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Optional<RefreshToken> maybe = refreshTokenRepository.findByToken(token);
        if (maybe.isEmpty())
            throw new IllegalArgumentException("Refresh token not found");
        RefreshToken entity = maybe.get();
        if (entity.isRevoked())
            throw new IllegalArgumentException("Refresh token revoked");
        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        // issue new tokens and revoke old
        String subject = jwtUtil.getSubject(token);
        // find user by subject
        Optional<User> maybeUser = userRepository.findByEmail(subject).isEmpty()
                ? userRepository.findByMobileNumber(subject)
                : userRepository.findByEmail(subject);
        User user = maybeUser.orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));

        entity.setRevoked(true);
        refreshTokenRepository.save(entity);

        String newAccess = jwtUtil.generateAccessToken(subject);
        String newRefresh = jwtUtil.generateRefreshToken(subject);
        storeRefreshToken(user, newRefresh);
        return new TokenResponse(newAccess, newRefresh, user.getId(), user.getName(), user.getEmail());
    }

    private void storeRefreshToken(User user, String token) {
        Date exp = jwtUtil.getExpiration(token);
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(exp.toInstant(), ZoneOffset.UTC);
        RefreshToken r = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        refreshTokenRepository.save(r);
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, otpLength);
        int val = random.nextInt(bound - (bound / 10)) + (bound / 10); // ensure leading digit not zero
        return String.valueOf(val);
    }
}
