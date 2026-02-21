package pro.play.pricing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.play.pricing.model.Pricing;

import java.util.List;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    List<Pricing> findByVenueId(Long venueId);
}

