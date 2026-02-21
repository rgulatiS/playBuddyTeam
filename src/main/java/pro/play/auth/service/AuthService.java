package pro.play.auth.service;

import pro.play.auth.dto.*;

public interface AuthService {
    TokenResponse registerByEmail(RegisterRequest req);
    OtpSentResponse registerByMobile(MobileRegisterRequest req);
    TokenResponse verifyOtp(VerifyOtpRequest req);
    TokenResponse loginByEmail(AuthRequest req);
    OtpSentResponse loginByMobile(MobileRegisterRequest req);
    TokenResponse refresh(RefreshRequest req);
}

