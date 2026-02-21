package pro.play.court.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.court.model.Court;

import java.util.List;

public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByVenueId(Long venueId);
}

