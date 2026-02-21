package pro.play.court.model;

import jakarta.persistence.*;
import lombok.*;
import pro.play.audit.AuditModel;
import pro.play.sport.model.Sport;
import pro.play.venue.model.Venue;

import java.math.BigDecimal;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Column(name = "price_per_hour")
    private BigDecimal pricePerHour;
}
