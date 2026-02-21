package pro.play.court.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.court.model.Court;
import pro.play.court.repository.CourtRepository;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtRepository courtRepository;

    @GetMapping
    public ResponseEntity<List<Court>> list() {
        return ResponseEntity.ok(courtRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Court> create(@RequestBody Court c) {
        return ResponseEntity.ok(courtRepository.save(c));
    }
}

