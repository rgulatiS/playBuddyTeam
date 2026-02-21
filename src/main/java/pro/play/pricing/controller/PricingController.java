package pro.play.pricing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.pricing.dto.PricingDto;
import pro.play.pricing.service.PricingService;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Configure and retrieve pricing rules for venues")
public class PricingController {

    private final PricingService pricingService;

    @Operation(summary = "Create a pricing rule")
    @PostMapping
    public ResponseEntity<PricingDto> create(@RequestBody PricingDto dto) {
        PricingDto created = pricingService.create(dto);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Get a pricing rule by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PricingDto> getById(@PathVariable Long id) {
        PricingDto dto = pricingService.getById(id);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all pricing rules for a venue")
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<PricingDto>> getByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(pricingService.getByVenueId(venueId));
    }

    @Operation(summary = "Delete a pricing rule")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pricingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
