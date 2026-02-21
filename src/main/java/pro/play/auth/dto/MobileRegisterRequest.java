package pro.play.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MobileRegisterRequest {
    @NotBlank
    private String mobileNumber;
    private String name;
}

