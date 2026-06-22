package vn.phankyanh.shopbongda.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.booking.domain.BookingStatusHistory;

@Repository
public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, Long> {

    @Query("""
    SELECT h FROM BookingStatusHistory h
    LEFT JOIN FETCH h.changedBy u
    WHERE h.booking.id = :bookingId
    ORDER BY h.createdAt ASC
    """)
    List<BookingStatusHistory> findByBookingIdOrderByCreatedAtAsc(
            @Param("bookingId") Long bookingId
    );
}
