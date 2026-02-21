package pro.play.pricing.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pricings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long venueId;

    // optional: pricing specific to a court
    private Long courtId;

    @Column(nullable = false)
    private BigDecimal pricePerHour;

    private String currency;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}

