package pro.play.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.play.booking.model.Booking;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Find bookings for a court on a date that overlap with the given time interval
    @Query("select b from Booking b where b.court.id = :courtId and b.date = :date and b.status <> pro.play.booking.model.BookingStatus.CANCELLED and b.endTime > :startTime and b.startTime < :endTime")
    List<Booking> findOverlapping(@Param("courtId") Long courtId, @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    // Find bookings for a venue between dates (inclusive)
    @Query("select b from Booking b where b.court.venue.id = :venueId and b.date >= :start and b.date <= :end")
    List<Booking> findByVenueAndDateBetween(@Param("venueId") Long venueId, @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("select b from Booking b join fetch b.court c join fetch c.venue v where b.user.id = :userId order by b.id desc")
    List<Booking> findByUserIdOrderByIdDesc(@Param("userId") Long userId);
}
