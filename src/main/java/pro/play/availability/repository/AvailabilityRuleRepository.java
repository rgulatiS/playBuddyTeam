package pro.play.availability.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.availability.model.AvailabilityRule;

import java.time.DayOfWeek;
import java.util.List;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {
    List<AvailabilityRule> findByCourtId(Long courtId);
    List<AvailabilityRule> findByCourtIdAndDayOfWeek(Long courtId, DayOfWeek dayOfWeek);
}

