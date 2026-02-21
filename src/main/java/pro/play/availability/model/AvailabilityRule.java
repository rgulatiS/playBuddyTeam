package pro.play.availability.model;

import jakarta.persistence.*;
import lombok.*;
import pro.play.court.model.Court;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "availability_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes;
}

