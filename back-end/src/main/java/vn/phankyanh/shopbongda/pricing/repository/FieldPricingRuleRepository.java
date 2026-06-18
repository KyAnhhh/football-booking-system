package vn.phankyanh.shopbongda.pricing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.pricing.domain.FieldPricingRule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FieldPricingRuleRepository
        extends JpaRepository<FieldPricingRule, Long> {

        List<FieldPricingRule> findByFieldIdAndIsActiveTrue(Long fieldId);

        List<FieldPricingRule> findByFieldIdAndDayOfWeekAndIsActiveTrue(
                Long fieldId,
                DayOfWeek dayOfWeek
        );

        Optional<FieldPricingRule>
                findFirstByFieldIdAndDayOfWeekAndIsActiveTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanOrderByStartTimeDesc(
                Long fieldId,
                DayOfWeek dayOfWeek,
                LocalTime bookingTime,
                LocalTime bookingTimeForEnd
        );
}
