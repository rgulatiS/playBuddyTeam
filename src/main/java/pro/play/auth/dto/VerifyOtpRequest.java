package pro.play.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank
    private String mobileNumber;
    @NotBlank
    private String code;
}

