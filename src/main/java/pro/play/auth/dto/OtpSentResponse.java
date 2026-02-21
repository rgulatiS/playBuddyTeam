package pro.play.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpSentResponse {
    private boolean sent;
    private String debugCode; // only for dev/testing
}

