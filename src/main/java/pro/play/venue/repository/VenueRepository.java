package pro.play.venue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.venue.model.Venue;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByOwnerId(Long ownerId);
}

