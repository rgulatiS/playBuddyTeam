package pro.play.pricing.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingDto {
    private Long id;
    private Long venueId;
    private Long courtId;
    private BigDecimal pricePerHour;
    private String currency;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}

