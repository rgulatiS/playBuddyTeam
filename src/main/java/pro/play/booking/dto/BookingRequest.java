package pro.play.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequest {
    @NotNull
    private Long courtId;
    @NotNull
    private Long userId;
    @NotNull
    private String bookedBy;
    @NotNull
    private Long bookedById;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    // amount in cents (optional) — if absent, server computes from court.pricePerHour
    private Long amountCents;
}

