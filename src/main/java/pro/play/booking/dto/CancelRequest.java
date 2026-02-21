package pro.play.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelRequest {
    @NotNull
    private Long bookingId;
    @NotNull
    private Long userId;
}

