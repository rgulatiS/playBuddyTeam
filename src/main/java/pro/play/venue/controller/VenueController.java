package pro.play.venue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Venues", description = "Manage sports venues and view their booking calendars")
public class VenueController {

    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;

    @Operation(summary = "List all venues")
    @GetMapping
    public ResponseEntity<List<Venue>> list() {
        return ResponseEntity.ok(venueRepository.findAll());
    }

    @Operation(summary = "Create a venue")
    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue v) {
        return ResponseEntity.ok(venueRepository.save(v));
    }

    @Operation(summary = "Create a venue (alias route)")
    @PostMapping("/create")
    public ResponseEntity<Venue> createAlias(@RequestBody Venue v) {
        return ResponseEntity.ok(venueRepository.save(v));
    }

    @Operation(summary = "Get a venue by ID")
    @GetMapping("/{venueId}")
    public ResponseEntity<Venue> getById(@PathVariable Long venueId) {
        return venueRepository.findById(venueId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get bookings for a venue within a date range (calendar view)")
    @GetMapping("/{venueId}/bookings")
    public ResponseEntity<List<Booking>> bookings(
            @PathVariable Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(bookingRepository.findByVenueAndDateBetween(venueId, start, end));
    }
}
