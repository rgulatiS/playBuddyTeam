package pro.play.availability.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityDto {
    private Long id;
    private Long venueId;
    private Long courtId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
}

