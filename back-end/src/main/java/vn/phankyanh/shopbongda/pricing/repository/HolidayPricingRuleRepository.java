package vn.phankyanh.shopbongda.pricing.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.pricing.domain.HolidayPricingRule;

@Repository
public interface HolidayPricingRuleRepository extends JpaRepository<HolidayPricingRule, Long> {

    @Query("""
    SELECT r FROM HolidayPricingRule r
    JOIN FETCH r.field f
    JOIN FETCH r.holiday h
    WHERE r.isActive = true
    ORDER BY h.holidayDate ASC, f.id ASC, r.startTime ASC
    """)
    List<HolidayPricingRule> findAllActive();

    @Query("""
    SELECT r FROM HolidayPricingRule r
    JOIN FETCH r.field f
    JOIN FETCH r.holiday h
    WHERE r.isActive = true
    AND (:fieldId IS NULL OR f.id = :fieldId)
    AND (:holidayId IS NULL OR h.id = :holidayId)
    ORDER BY h.holidayDate ASC, f.id ASC, r.startTime ASC
    """)
    List<HolidayPricingRule> searchActive(
            @Param("fieldId") Long fieldId,
            @Param("holidayId") Long holidayId
    );

    @Query("""
    SELECT r FROM HolidayPricingRule r
    WHERE r.field.id = :fieldId
    AND r.holiday.id = :holidayId
    AND r.isActive = true
    AND r.startTime < :endTime
    AND r.endTime > :startTime
    ORDER BY r.startTime DESC
    """)
    List<HolidayPricingRule> findActiveOverlaps(
            @Param("fieldId") Long fieldId,
            @Param("holidayId") Long holidayId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("""
    SELECT r FROM HolidayPricingRule r
    JOIN r.holiday h
    WHERE r.field.id = :fieldId
    AND h.holidayDate = :holidayDate
    AND h.isActive = true
    AND r.isActive = true
    AND r.startTime <= :slotTime
    AND r.endTime > :slotTime
    ORDER BY r.startTime DESC
    """)
    Optional<HolidayPricingRule> findFirstApplicableRule(
            @Param("fieldId") Long fieldId,
            @Param("holidayDate") LocalDate holidayDate,
            @Param("slotTime") LocalTime slotTime
    );
}
