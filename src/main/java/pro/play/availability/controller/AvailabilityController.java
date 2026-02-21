package pro.play.availability.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.availability.dto.AvailabilityDto;
import pro.play.availability.service.AvailabilityService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Tag(name = "Availability", description = "Manage court availability slots and rules")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Operation(summary = "Create an availability slot")
    @PostMapping
    public ResponseEntity<AvailabilityDto> create(@RequestBody AvailabilityDto dto) {
        AvailabilityDto created = availabilityService.create(dto);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Create an availability slot (alias route)")
    @PostMapping("/create")
    public ResponseEntity<AvailabilityDto> createAlias(@RequestBody AvailabilityDto dto) {
        AvailabilityDto created = availabilityService.create(dto);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Get availability slot by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityDto> getById(@PathVariable Long id) {
        AvailabilityDto dto = availabilityService.getById(id);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all availability slots for a venue")
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<AvailabilityDto>> getByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(availabilityService.getByVenue(venueId));
    }

    @Operation(summary = "Get availability slots for a venue on a specific date")
    @GetMapping("/venue/{venueId}/date/{date}")
    public ResponseEntity<List<AvailabilityDto>> getByVenueAndDate(
            @PathVariable Long venueId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getByVenueAndDate(venueId, date));
    }

    @Operation(summary = "Delete an availability slot")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        availabilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
