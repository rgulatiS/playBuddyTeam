package pro.play.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank
    private String email;

    private String mobileNumber;

    private String password;

    private String name;

    @NotBlank
    private String code;
}
