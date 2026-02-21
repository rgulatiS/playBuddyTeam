package pro.play.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pro.play.auth.dto.*;
import pro.play.auth.model.OtpCode;
import pro.play.auth.model.RefreshToken;
import pro.play.auth.repository.OtpCodeRepository;
import pro.play.auth.repository.RefreshTokenRepository;
import pro.play.auth.service.impl.AuthServiceImpl;
import pro.play.security.JwtUtil;
import pro.play.user.model.Role;
import pro.play.user.model.User;
import pro.play.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    private UserRepository userRepository;
    private OtpCodeRepository otpCodeRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private AuthServiceImpl authService;

    @BeforeEach
    public void setup() throws Exception {
        userRepository = mock(UserRepository.class);
        otpCodeRepository = mock(OtpCodeRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        authenticationManager = mock(AuthenticationManager.class);

        authService = new AuthServiceImpl(userRepository, otpCodeRepository, refreshTokenRepository, passwordEncoder, jwtUtil, authenticationManager);
        // set otp config fields via reflection
        setField(authService, "otpExpiryMinutes", 5);
        setField(authService, "otpMaxAttempts", 5);

        // common mocks used by many tests: ensure jwtUtil.getExpiration returns a Date
        when(jwtUtil.getExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 604800000L));
        // mock authenticationManager.authenticate to return the passed Authentication (no exception)
        when(authenticationManager.authenticate(any(Authentication.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    public void testRegisterByEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("secret");
        req.setName("Tester");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(jwtUtil.generateAccessToken(anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh-token");

        TokenResponse resp = authService.registerByEmail(req);

        assertNotNull(resp);
        assertEquals("access-token", resp.getAccessToken());
        assertEquals("refresh-token", resp.getRefreshToken());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("test@example.com", saved.getEmail());
        assertEquals("hashed", saved.getPasswordHash());
        assertEquals(Role.USER, saved.getRole());
    }

    @Test
    public void testLoginByEmail() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user@example.com");
        req.setPassword("pass");

        User user = User.builder().id(1L).email("user@example.com").passwordHash("hashed").role(Role.USER).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(anyString())).thenReturn("access");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh");

        // authenticationManager.authenticate mocked in setup to return an Authentication

        TokenResponse resp = authService.loginByEmail(req);
        assertNotNull(resp);
        assertEquals("access", resp.getAccessToken());
        assertEquals("refresh", resp.getRefreshToken());
    }

    @Test
    public void testRegisterAndVerifyOtp() {
        MobileRegisterRequest reg = new MobileRegisterRequest();
        reg.setMobileNumber("+10000000000");
        reg.setName("MobileUser");

        OtpSentResponse sent = authService.registerByMobile(reg);
        assertTrue(sent.isSent());
        assertNotNull(sent.getDebugCode());

        // mock otp lookup to return the one saved: construct an OtpCode with same code
        OtpCode otp = OtpCode.builder()
                .id(1L)
                .mobileNumber("+10000000000")
                .code(sent.getDebugCode())
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(5))
                .attemptsLeft(5)
                .used(false)
                .build();

        when(otpCodeRepository.findTopByMobileNumberOrderByIdDesc("+10000000000")).thenReturn(Optional.of(otp));
        when(userRepository.findByMobileNumber("+10000000000")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = (User) inv.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(jwtUtil.generateAccessToken(anyString())).thenReturn("a");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("r");

        VerifyOtpRequest vreq = new VerifyOtpRequest();
        vreq.setMobileNumber("+10000000000");
        vreq.setCode(sent.getDebugCode());

        TokenResponse tresp = authService.verifyOtp(vreq);
        assertNotNull(tresp);
        assertEquals("a", tresp.getAccessToken());
        assertEquals("r", tresp.getRefreshToken());

        // ensure otp was marked used and saved
        ArgumentCaptor<OtpCode> otpCaptor = ArgumentCaptor.forClass(OtpCode.class);
        verify(otpCodeRepository, atLeastOnce()).save(otpCaptor.capture());
        OtpCode savedOtp = otpCaptor.getValue();
        assertTrue(savedOtp.isUsed());
    }

    @Test
    public void testRefresh() {
        RefreshRequest rreq = new RefreshRequest();
        rreq.setRefreshToken("ref-token");

        when(jwtUtil.isRefreshToken("ref-token")).thenReturn(true);
        when(jwtUtil.validate("ref-token")).thenReturn(true);
        when(refreshTokenRepository.findByToken("ref-token")).thenReturn(Optional.of(
                RefreshToken.builder().id(1L).token("ref-token").revoked(false).expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1)).build()
        ));
        when(jwtUtil.getSubject("ref-token")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(User.builder().id(2L).email("user@example.com").build()));
        when(jwtUtil.generateAccessToken(anyString())).thenReturn("newA");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("newR");

        TokenResponse resp = authService.refresh(rreq);
        assertEquals("newA", resp.getAccessToken());
        assertEquals("newR", resp.getRefreshToken());

        // ensure old token revoked
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(captor.capture());
        List<RefreshToken> savedTokens = captor.getAllValues();
        // first save: the old token revoked
        assertTrue(savedTokens.get(0).isRevoked());
        // second save: the newly created refresh token should be persisted and not revoked
        assertEquals("newR", savedTokens.get(1).getToken());
        assertFalse(savedTokens.get(1).isRevoked());
    }
}
