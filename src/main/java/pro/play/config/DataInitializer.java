package pro.play.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pro.play.city.model.City;
import pro.play.city.repository.CityRepository;
import pro.play.court.model.Court;
import pro.play.court.repository.CourtRepository;
import pro.play.sport.model.Sport;
import pro.play.sport.repository.SportRepository;
import pro.play.venue.model.Venue;
import pro.play.venue.repository.VenueRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final SportRepository sportRepository;
        private final CityRepository cityRepository;
        private final VenueRepository venueRepository;
        private final CourtRepository courtRepository;
        private final pro.play.availability.repository.AvailabilityRuleRepository availabilityRuleRepository;

        @Override
        public void run(String... args) {
                if (sportRepository.count() == 0) {
                        seedSports();
                }
                if (cityRepository.count() == 0) {
                        seedCities();
                }
                if (venueRepository.count() == 0) {
                        seedVenues();
                }
                if (courtRepository.count() == 0) {
                        seedCourts();
                }
                if (availabilityRuleRepository.count() == 0) {
                        seedAvailabilityRules();
                }

                log.info("Data seeding check completed.");
        }

        private void seedSports() {
                log.info("Seeding sports...");
                sportRepository.save(Sport.builder().name("Badminton").build());
                sportRepository.save(Sport.builder().name("Tennis").build());
                sportRepository.save(Sport.builder().name("Football").build());
                sportRepository.save(Sport.builder().name("Cricket").build());
        }

        private void seedCities() {
                log.info("Seeding cities...");
                cityRepository.save(City.builder().name("Mumbai").country("India").build());
        }

        private void seedVenues() {
                log.info("Seeding venues...");
                List<City> cities = cityRepository.findAll();
                if (cities.isEmpty())
                        return;
                City mumbai = cities.get(0);
                venueRepository.save(Venue.builder()
                                .name("Elite Sports Arena")
                                .address("Andheri West, Mumbai")
                                .city(mumbai)
                                .imageUrl("/images/badminton.png") // Venue-wide image
                                .build());
                venueRepository.save(Venue.builder()
                                .name("City Play Arena")
                                .address("Bandra East, Mumbai")
                                .city(mumbai)
                                .imageUrl("/images/football.png") // Venue-wide image
                                .build());
        }

        private void seedCourts() {
                log.info("Seeding courts...");
                List<Venue> venues = venueRepository.findAll();
                List<Sport> sports = sportRepository.findAll();
                if (venues.size() < 2 || sports.isEmpty())
                        return;

                Venue v1 = venues.get(0);
                Venue v2 = venues.get(1);

                Sport badminton = sports.stream().filter(s -> s.getName().equals("Badminton")).findFirst().orElse(null);
                Sport tennis = sports.stream().filter(s -> s.getName().equals("Tennis")).findFirst().orElse(null);
                Sport football = sports.stream().filter(s -> s.getName().equals("Football")).findFirst().orElse(null);
                Sport cricket = sports.stream().filter(s -> s.getName().equals("Cricket")).findFirst().orElse(null);

                courtRepository.saveAll(Arrays.asList(
                                Court.builder().name("Badminton Court 1").venue(v1).sport(badminton)
                                                .pricePerHour(new BigDecimal("500"))
                                                .imageUrl("/images/badminton.png").build(),
                                Court.builder().name("Badminton Court 2").venue(v1).sport(badminton)
                                                .pricePerHour(new BigDecimal("500"))
                                                .imageUrl("/images/badminton.png").build(),
                                Court.builder().name("Tennis Court A").venue(v1).sport(tennis)
                                                .pricePerHour(new BigDecimal("800"))
                                                .imageUrl("/images/tennis.png").build(),
                                Court.builder().name("Football Pitch 1").venue(v2).sport(football)
                                                .pricePerHour(new BigDecimal("1500"))
                                                .imageUrl("/images/football.png").build(),
                                Court.builder().name("Cricket Box 1").venue(v2).sport(cricket)
                                                .pricePerHour(new BigDecimal("1200"))
                                                .imageUrl("/images/cricket.png").build()));
        }

        private void seedAvailabilityRules() {
                log.info("Seeding availability rules for all courts...");
                List<Court> courts = courtRepository.findAll();
                for (Court court : courts) {
                        for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
                                availabilityRuleRepository.save(pro.play.availability.model.AvailabilityRule.builder()
                                                .court(court)
                                                .dayOfWeek(day)
                                                .startTime(java.time.LocalTime.of(6, 0))
                                                .endTime(java.time.LocalTime.of(22, 0))
                                                .slotDurationMinutes(60)
                                                .build());
                        }
                }
        }
}
