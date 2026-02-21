package pro.play.venue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.booking.model.Booking;
import pro.play.booking.repository.BookingRepository;
import pro.play.venue.model.Venue;
import pro.play.venue.repository.VenueRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<List<Venue>> list() {
        return ResponseEntity.ok(venueRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue v) {
        return ResponseEntity.ok(venueRepository.save(v));
    }

    // explicit create route requested
    @PostMapping("/create")
    public ResponseEntity<Venue> createAlias(@RequestBody Venue v) {
        return ResponseEntity.ok(venueRepository.save(v));
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<Venue> getById(@PathVariable Long venueId) {
        return venueRepository.findById(venueId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // View bookings for venue in a date range (calendar)
    @GetMapping("/{venueId}/bookings")
    public ResponseEntity<List<Booking>> bookings(
            @PathVariable Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(bookingRepository.findByVenueAndDateBetween(venueId, start, end));
    }
}
