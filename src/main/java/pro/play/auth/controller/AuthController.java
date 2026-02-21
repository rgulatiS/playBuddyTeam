package pro.play.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.auth.dto.*;
import pro.play.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login, OTP verification and token refresh")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user via email & password")
    @PostMapping("/register/email")
    public ResponseEntity<TokenResponse> registerByEmail(@Valid @RequestBody RegisterRequest req) {
        TokenResponse resp = authService.registerByEmail(req);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Start mobile registration — sends OTP")
    @PostMapping("/register/mobile")
    public ResponseEntity<OtpSentResponse> registerByMobile(@Valid @RequestBody MobileRegisterRequest req) {
        OtpSentResponse resp = authService.registerByMobile(req);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Verify OTP and complete mobile registration / login")
    @PostMapping("/verify-otp")
    public ResponseEntity<TokenResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        TokenResponse resp = authService.verifyOtp(req);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Login with email & password")
    @PostMapping("/login/email")
    public ResponseEntity<TokenResponse> loginByEmail(@Valid @RequestBody AuthRequest req) {
        TokenResponse resp = authService.loginByEmail(req);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Start mobile login — sends OTP")
    @PostMapping("/login/mobile")
    public ResponseEntity<OtpSentResponse> loginByMobile(@Valid @RequestBody MobileRegisterRequest req) {
        OtpSentResponse resp = authService.loginByMobile(req);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Refresh an expired access token using a refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        TokenResponse resp = authService.refresh(req);
        return ResponseEntity.ok(resp);
    }
}
