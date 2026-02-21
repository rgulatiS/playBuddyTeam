package pro.play.availability.controller;

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
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityDto> create(@RequestBody AvailabilityDto dto) {
        AvailabilityDto created = availabilityService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityDto> getById(@PathVariable Long id) {
        AvailabilityDto dto = availabilityService.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<AvailabilityDto>> getByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(availabilityService.getByVenue(venueId));
    }

    @GetMapping("/venue/{venueId}/date/{date}")
    public ResponseEntity<List<AvailabilityDto>> getByVenueAndDate(
            @PathVariable Long venueId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getByVenueAndDate(venueId, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        availabilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

