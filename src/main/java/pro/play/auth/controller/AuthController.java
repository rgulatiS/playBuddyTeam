package pro.play.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.auth.dto.*;
import pro.play.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/email")
    public ResponseEntity<TokenResponse> registerByEmail(@Valid @RequestBody RegisterRequest req) {
        TokenResponse resp = authService.registerByEmail(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register/mobile")
    public ResponseEntity<OtpSentResponse> registerByMobile(@Valid @RequestBody MobileRegisterRequest req) {
        OtpSentResponse resp = authService.registerByMobile(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<TokenResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        TokenResponse resp = authService.verifyOtp(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login/email")
    public ResponseEntity<TokenResponse> loginByEmail(@Valid @RequestBody AuthRequest req) {
        TokenResponse resp = authService.loginByEmail(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login/mobile")
    public ResponseEntity<OtpSentResponse> loginByMobile(@Valid @RequestBody MobileRegisterRequest req) {
        OtpSentResponse resp = authService.loginByMobile(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        TokenResponse resp = authService.refresh(req);
        return ResponseEntity.ok(resp);
    }
}
