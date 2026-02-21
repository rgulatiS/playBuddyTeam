package pro.play.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.booking.model.Booking;
import pro.play.booking.model.BookingStatus;
import pro.play.booking.repository.BookingRepository;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<List<Booking>> list() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody Booking b) {
        // NOTE: real implementation should check availability and create payment
        b.setStatus(BookingStatus.PENDING);
        return ResponseEntity.ok(bookingRepository.save(b));
    }
}
