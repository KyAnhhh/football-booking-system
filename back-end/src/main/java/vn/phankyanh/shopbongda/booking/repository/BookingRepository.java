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

    @Query("""
    SELECT DISTINCT b FROM Booking b
    JOIN FETCH b.user u
    JOIN FETCH b.field f
    LEFT JOIN FETCH b.payment p
    LEFT JOIN FETCH b.extraServices bes
    LEFT JOIN FETCH bes.extraService es
    WHERE b.id = :bookingId
    AND u.id = :userId
    """)
    Optional<Booking> findUserBookingDetail(
            @Param("bookingId") Long bookingId,
            @Param("userId") Long userId
    );

    @Query("""
    SELECT DISTINCT b FROM Booking b
    JOIN FETCH b.user u
    JOIN FETCH b.field f
    LEFT JOIN FETCH b.payment p
    LEFT JOIN FETCH b.extraServices bes
    LEFT JOIN FETCH bes.extraService es
    WHERE b.id = :bookingId
    """)
    Optional<Booking> findAdminBookingDetail(
            @Param("bookingId") Long bookingId
    );

    boolean existsByFieldId(Long fieldId);

    boolean existsByPaymentId(Long paymentId);

    long countByStartTimeGreaterThanEqualAndStartTimeLessThan(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT b FROM Booking b
    WHERE b.id = :bookingId
    """)
    Optional<Booking> findByIdForUpdate(
            @Param("bookingId") Long bookingId
    );

    @Query("""
    SELECT b.id FROM Booking b
    WHERE b.status = 'PENDING'
    AND b.createdAt < :cutoff
    """)
    List<Long> findPendingIdsCreatedBefore(
            @Param("cutoff") LocalDateTime cutoff
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
    SELECT COALESCE(SUM(b.totalPrice), 0)
    FROM Booking b
    WHERE b.startTime >= :startOfDay
    AND b.startTime < :endOfDay
    AND b.status IN ('CONFIRMED', 'COMPLETED')
    """)
    java.math.BigDecimal sumTodayBookingRevenue(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
    SELECT b FROM Booking b
    JOIN FETCH b.user u
    JOIN FETCH b.field f
    WHERE b.startTime >= :startOfDay
    AND b.startTime < :endOfDay
    AND (:status IS NULL OR b.status = :status)
    ORDER BY b.startTime ASC
    """)
    List<Booking> findTodayDashboardBookings(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("status") BookingStatus status,
            Pageable pageable
    );

    @Query("""
    SELECT b FROM Booking b
    JOIN FETCH b.user u
    JOIN FETCH b.field f
    WHERE b.startTime < :endOfDay
    AND b.endTime > :startOfDay
    AND b.status <> 'CANCELLED'
    AND b.status <> 'EXPIRED'
    ORDER BY f.id ASC, b.startTime ASC
    """)
    List<Booking> findCalendarBookings(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
    SELECT YEAR(b.createdAt),
           MONTH(b.createdAt),
           SUM(b.totalPrice)
    FROM Booking b
    WHERE b.status = 'COMPLETED'
    GROUP BY YEAR(b.createdAt), MONTH(b.createdAt)
    ORDER BY YEAR(b.createdAt), MONTH(b.createdAt)
    """)
    List<Object[]> getMonthlyRevenue();

}
