package pro.play.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCourtIdAndEndTimeAfterAndStartTimeBefore(Long courtId, LocalDateTime after, LocalDateTime before);
}

