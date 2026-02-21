package pro.play.pricing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.pricing.dto.PricingDto;
import pro.play.pricing.service.PricingService;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping
    public ResponseEntity<PricingDto> create(@RequestBody PricingDto dto) {
        PricingDto created = pricingService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricingDto> getById(@PathVariable Long id) {
        PricingDto dto = pricingService.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<PricingDto>> getByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(pricingService.getByVenueId(venueId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pricingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

