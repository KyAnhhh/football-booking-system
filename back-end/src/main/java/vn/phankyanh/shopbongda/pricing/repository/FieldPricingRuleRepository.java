package vn.phankyanh.shopbongda.pricing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
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

        @Query("SELECT r FROM FieldPricingRule r " +
               "WHERE r.isActive = true " +
               "ORDER BY r.field.id ASC, r.dayOfWeek ASC, r.startTime ASC")
        List<FieldPricingRule> findAllActive();

        @Query("SELECT r FROM FieldPricingRule r " +
               "WHERE r.field.id = :fieldId " +
               "AND r.isActive = true " +
               "ORDER BY r.dayOfWeek ASC, r.startTime ASC")
        List<FieldPricingRule> findActiveByFieldId(
                @Param("fieldId") Long fieldId);

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

        @Query("SELECT r FROM FieldPricingRule r " +
               "WHERE r.field.id = :fieldId " +
               "AND r.dayOfWeek = :dayOfWeek " +
               "AND r.isActive = true " +
               "AND r.startTime <= :slotTime " +
               "AND r.endTime > :slotTime " +
               "ORDER BY r.startTime DESC")
        Optional<FieldPricingRule> findFirstByFieldIdAndDayOfWeekAndSlotTime(
                @Param("fieldId") Long fieldId,
                @Param("dayOfWeek") DayOfWeek dayOfWeek,
                @Param("slotTime") LocalTime slotTime
        );

        @Query("SELECT r FROM FieldPricingRule r " +
               "WHERE r.field.id = :fieldId " +
               "AND r.dayOfWeek = :dayOfWeek " +
               "AND r.isActive = true " +
               "AND r.startTime < :endTime " +
               "AND r.endTime > :startTime " +
               "ORDER BY r.startTime DESC")
        List<FieldPricingRule> findByFieldIdAndDayOfWeekAndTimeOverlaps(
                @Param("fieldId") Long fieldId,
                @Param("dayOfWeek") DayOfWeek dayOfWeek,
                @Param("startTime") LocalTime startTime,
                @Param("endTime") LocalTime endTime
        );
}
