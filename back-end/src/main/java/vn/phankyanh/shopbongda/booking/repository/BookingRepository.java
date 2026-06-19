package vn.phankyanh.shopbongda.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.projection.BookingTimeView;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // List<Booking> findByFieldIdAndStartTimeLessThanAndEndTimeGreaterThan(
    //     Long fieldId, LocalDateTime endTime, LocalDateTime startTime);

    List<Booking> findByUserId(Long userId);

    boolean existsByFieldId(Long fieldId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT b FROM Booking b
    WHERE b.id = :bookingId
    """)
    Optional<Booking> findByIdForUpdate(
            @Param("bookingId") Long bookingId
    );

    @Query("""
    SELECT b FROM Booking b
    WHERE (:status IS NULL OR b.status = :status)
    AND (:keyword IS NULL OR LOWER(b.field.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Booking> searchBookings(@Param("keyword") String keyword, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    SELECT b.startTime AS startTime,
        b.endTime AS endTime,
        b.status AS status
    FROM Booking b
    WHERE b.field.id = :fieldId
    AND b.startTime < :endOfDay
    AND b.endTime > :startOfDay
    """)
    List<BookingTimeView> findBookingsInDayLite(
            @Param("fieldId") Long fieldId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT b FROM Booking b
    WHERE b.field.id = :fieldId
    AND b.startTime < :endTime
    AND b.endTime > :startTime
    AND b.status <> 'CANCELLED'
    AND b.status <> 'EXPIRED'
    """)
    List<Booking> findConflictsForUpdate(
            Long fieldId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query("""
    SELECT MONTH(b.createdAt),
    SUM(b.totalPrice)
    FROM Booking b
    WHERE b.status = 'COMPLETED'
    GROUP BY MONTH(b.createdAt)
    ORDER BY MONTH(b.createdAt)
    """)
    List<Object[]> getMonthlyRevenue();

}
