package pro.play.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank
    private String name;

    @Email
    private String email;

    @Size(min = 6, max = 15)
    private String mobileNumber;

    private String role; // USER, VENUE_OWNER, ADMIN

    private Long cityId;
}

