package pro.play.pricing.service;

import pro.play.pricing.dto.PricingDto;

import java.util.List;

public interface PricingService {
    PricingDto create(PricingDto dto);
    PricingDto getById(Long id);
    List<PricingDto> getByVenueId(Long venueId);
    void delete(Long id);
}

