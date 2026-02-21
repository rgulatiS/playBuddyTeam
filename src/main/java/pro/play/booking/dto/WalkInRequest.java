package pro.play.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WalkInRequest {
    @NotNull
    private Long courtId;
    private String customerName;
    private String customerEmail;
    private String customerMobile;
    @NotNull
    private String bookedBy; // name of customer who paid
    @NotNull
    private Long bookedById; // id of person performing booking
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private Long amountCents;
}

