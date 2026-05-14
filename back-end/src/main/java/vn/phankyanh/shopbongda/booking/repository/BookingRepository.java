package vn.phankyanh.shopbongda.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.projection.BookingTimeView;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByFieldIdAndStartTimeLessThanAndEndTimeGreaterThan(
        Long fieldId, LocalDateTime endTime, LocalDateTime startTime);

    List<Booking> findByUserId(Long userId);

    boolean existsByFieldId(Long fieldId);

    @Query("""
    SELECT b FROM Booking b
    WHERE (:status IS NULL OR b.status = :status)
    AND (:keyword IS NULL OR LOWER(b.field.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Booking> searchBookings(String keyword, BookingStatus status, Pageable pageable);

    @Query("""
    SELECT b.startTime AS startTime,
        b.endTime AS endTime,
        b.status AS status
    FROM Booking b
    WHERE b.field.id = :fieldId
    AND b.status <> 'CANCELLED'
    AND b.startTime < :endOfDay
    AND b.endTime > :startOfDay
    """)
    List<BookingTimeView> findBookingsInDayLite(
            @Param("fieldId") Long fieldId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

}
