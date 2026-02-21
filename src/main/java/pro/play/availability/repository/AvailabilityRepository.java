package pro.play.availability.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.availability.model.Availability;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByVenueIdAndDate(Long venueId, LocalDate date);
    List<Availability> findByVenueId(Long venueId);
    List<Availability> findByCourtIdAndDate(Long courtId, LocalDate date);
}
