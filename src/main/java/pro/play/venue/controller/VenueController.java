package pro.play.venue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.venue.model.Venue;
import pro.play.venue.repository.VenueRepository;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueRepository venueRepository;

    @GetMapping
    public ResponseEntity<List<Venue>> list() {
        return ResponseEntity.ok(venueRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue v) {
        return ResponseEntity.ok(venueRepository.save(v));
    }
}

