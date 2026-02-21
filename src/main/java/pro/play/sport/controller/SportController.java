package pro.play.sport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.sport.model.Sport;
import pro.play.sport.repository.SportRepository;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
@Tag(name = "Sports", description = "List available sports")
public class SportController {

    private final SportRepository sportRepository;

    @Operation(summary = "List all available sports")
    @GetMapping
    public ResponseEntity<List<Sport>> list() {
        return ResponseEntity.ok(sportRepository.findAll());
    }
}
