package pro.play.court.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.availability.repository.AvailabilityRuleRepository;
import pro.play.court.model.Court;
import pro.play.court.repository.CourtRepository;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
@Tag(name = "Courts", description = "Manage courts and query available courts by sport and date")
public class CourtController {

    private final CourtRepository courtRepository;
    private final AvailabilityRuleRepository availabilityRuleRepository;
    private final pro.play.booking.repository.BookingRepository bookingRepository;

    @Operation(summary = "List all courts")
    @GetMapping
    public ResponseEntity<List<Court>> list() {
        return ResponseEntity.ok(courtRepository.findAll());
    }

    @Operation(summary = "Create a court")
    @PostMapping
    public ResponseEntity<Court> create(@RequestBody Court c) {
        return ResponseEntity.ok(courtRepository.save(c));
    }

    @Operation(summary = "Create a court (alias route)")
    @PostMapping("/create")
    public ResponseEntity<Court> createAlias(@RequestBody Court c) {
        return ResponseEntity.ok(courtRepository.save(c));
    }

    @Operation(summary = "Get courts belonging to a venue")
    @GetMapping("/{venueId}")
    public ResponseEntity<List<Court>> getByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(courtRepository.findByVenueId(venueId));
    }

    @Operation(summary = "Find courts available for a given sport and date with slots")
    @GetMapping("/available")
    public ResponseEntity<List<pro.play.court.dto.CourtResponse>> available(
            @RequestParam(required = false) Long sportId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {

        if (sportId == null || date == null) {
            return ResponseEntity.ok(java.util.List.of());
        }

        java.util.List<Court> courts = courtRepository.findBySportId(sportId);
        java.util.List<pro.play.court.dto.CourtResponse> responses = new java.util.ArrayList<>();

        for (Court court : courts) {
            java.util.List<pro.play.availability.model.AvailabilityRule> rules = availabilityRuleRepository
                    .findByCourtIdAndDayOfWeek(court.getId(), date.getDayOfWeek());

            java.util.List<String> availableSlots = new java.util.ArrayList<>();

            for (pro.play.availability.model.AvailabilityRule rule : rules) {
                java.time.LocalTime start = rule.getStartTime();
                java.time.LocalTime end = rule.getEndTime();
                int duration = rule.getSlotDurationMinutes() != null ? rule.getSlotDurationMinutes() : 60;

                while (start.plusMinutes(duration).isBefore(end) || start.plusMinutes(duration).equals(end)) {
                    java.time.LocalTime slotEnd = start.plusMinutes(duration);

                    // Check if slot is already booked
                    boolean isBooked = !bookingRepository.findOverlapping(court.getId(), date, start, slotEnd)
                            .isEmpty();

                    if (!isBooked) {
                        availableSlots.add(start.toString());
                    }
                    start = slotEnd;
                }
            }

            if (!availableSlots.isEmpty()) {
                responses.add(pro.play.court.dto.CourtResponse.builder()
                        .court(court)
                        .availableSlots(availableSlots)
                        .build());
            }
        }

        return ResponseEntity.ok(responses);
    }
}
