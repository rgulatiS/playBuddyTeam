package pro.play.availability.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.play.availability.dto.AvailabilityDto;
import pro.play.availability.model.Availability;
import pro.play.availability.repository.AvailabilityRepository;
import pro.play.availability.service.AvailabilityService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    @Override
    public AvailabilityDto create(AvailabilityDto dto) {
        Availability a = Availability.builder()
                .id(dto.getId())
                .venueId(dto.getVenueId())
                .courtId(dto.getCourtId())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .available(dto.isAvailable())
                .build();
        Availability saved = availabilityRepository.save(a);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public AvailabilityDto getById(Long id) {
        Optional<Availability> o = availabilityRepository.findById(id);
        return o.map(this::toDto).orElse(null);
    }

    @Override
    public List<AvailabilityDto> getByVenueAndDate(Long venueId, LocalDate date) {
        return availabilityRepository.findByVenueIdAndDate(venueId, date).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityDto> getByVenue(Long venueId) {
        return availabilityRepository.findByVenueId(venueId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        availabilityRepository.deleteById(id);
    }

    private AvailabilityDto toDto(Availability a) {
        return AvailabilityDto.builder()
                .id(a.getId())
                .venueId(a.getVenueId())
                .courtId(a.getCourtId())
                .date(a.getDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .available(a.isAvailable())
                .build();
    }
}

