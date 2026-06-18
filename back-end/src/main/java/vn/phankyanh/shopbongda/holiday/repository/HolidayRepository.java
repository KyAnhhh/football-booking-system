package vn.phankyanh.shopbongda.holiday.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.holiday.domain.Holiday;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByHolidayDate(LocalDate holidayDate);

    boolean existsByHolidayDateAndIsActiveTrue(LocalDate holidayDate);

    Optional<Holiday> findByHolidayDateAndIsActiveTrue(
            LocalDate holidayDate);

    List<Holiday> findByIsActiveTrue();
}
