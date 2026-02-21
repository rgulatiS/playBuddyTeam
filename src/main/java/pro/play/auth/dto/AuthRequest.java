package pro.play.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String username; // email or phone
    @NotBlank
    private String password;
}
