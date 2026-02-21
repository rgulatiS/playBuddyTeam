package pro.play.pricing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.play.pricing.dto.PricingDto;
import pro.play.pricing.model.Pricing;
import pro.play.pricing.repository.PricingRepository;
import pro.play.pricing.service.PricingService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final PricingRepository pricingRepository;

    @Override
    public PricingDto create(PricingDto dto) {
        Pricing pricing = Pricing.builder()
                .id(dto.getId())
                .venueId(dto.getVenueId())
                .courtId(dto.getCourtId())
                .pricePerHour(dto.getPricePerHour())
                .currency(dto.getCurrency())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .build();
        Pricing saved = pricingRepository.save(pricing);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public PricingDto getById(Long id) {
        Optional<Pricing> p = pricingRepository.findById(id);
        return p.map(this::toDto).orElse(null);
    }

    @Override
    public List<PricingDto> getByVenueId(Long venueId) {
        return pricingRepository.findByVenueId(venueId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        pricingRepository.deleteById(id);
    }

    private PricingDto toDto(Pricing p) {
        return PricingDto.builder()
                .id(p.getId())
                .venueId(p.getVenueId())
                .courtId(p.getCourtId())
                .pricePerHour(p.getPricePerHour())
                .currency(p.getCurrency())
                .effectiveFrom(p.getEffectiveFrom())
                .effectiveTo(p.getEffectiveTo())
                .build();
    }
}

