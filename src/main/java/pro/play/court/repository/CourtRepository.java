package pro.play.court.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.play.court.model.Court;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByVenueId(Long venueId);

    // added: find courts by sport
    List<Court> findBySportId(Long sportId);

    // Lock the court row for update to avoid concurrent bookings
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Court c where c.id = :id")
    Optional<Court> findByIdForUpdate(@Param("id") Long id);
}
