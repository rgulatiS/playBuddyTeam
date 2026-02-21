package pro.play.court.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.availability.repository.AvailabilityRepository;
import pro.play.availability.repository.AvailabilityRuleRepository;
import pro.play.court.model.Court;
import pro.play.court.repository.CourtRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
@Tag(name = "Courts", description = "Manage courts and query available courts by sport and date")
public class CourtController {

    private final CourtRepository courtRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityRuleRepository availabilityRuleRepository;

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

    @Operation(summary = "Find courts available for a given sport and date")
    @GetMapping("/available")
    public ResponseEntity<List<Court>> available(
            @RequestParam Long sportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Court> courts = courtRepository.findBySportId(sportId);
        List<Court> available = courts.stream().filter(c -> {
            boolean hasSlot = !availabilityRepository.findByCourtIdAndDate(c.getId(), date).isEmpty();
            boolean hasRule = !availabilityRuleRepository.findByCourtIdAndDayOfWeek(c.getId(), date.getDayOfWeek())
                    .isEmpty();
            return hasSlot || hasRule;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(available);
    }
}
