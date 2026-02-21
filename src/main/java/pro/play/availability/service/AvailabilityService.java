package pro.play.availability.service;

import pro.play.availability.dto.AvailabilityDto;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    AvailabilityDto create(AvailabilityDto dto);
    AvailabilityDto getById(Long id);
    List<AvailabilityDto> getByVenueAndDate(Long venueId, LocalDate date);
    List<AvailabilityDto> getByVenue(Long venueId);
    void delete(Long id);
}

