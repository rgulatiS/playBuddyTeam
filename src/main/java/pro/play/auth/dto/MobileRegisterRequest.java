package pro.play.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MobileRegisterRequest {
    @NotBlank
    private String mobileNumber;
    @NotBlank
    private String email;
    private String name;
}
