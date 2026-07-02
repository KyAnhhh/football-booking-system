package vn.phankyanh.shopbongda.extra.repository;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.extra.domain.BookingExtraService;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;

@Repository
public interface BookingExtraServiceRepository
        extends JpaRepository<BookingExtraService, Long> {

    List<BookingExtraService> findByBookingId(Long bookingId);

    @Query("""
            select coalesce(sum(bes.totalPrice), 0)
            from BookingExtraService bes
            where bes.booking.status in :statuses
            """)
    BigDecimal sumRecognizedRevenue(List<BookingStatus> statuses);

    @Query("""
            select coalesce(sum(bes.totalCost), 0)
            from BookingExtraService bes
            where bes.booking.status in :statuses
            """)
    BigDecimal sumRecognizedCost(List<BookingStatus> statuses);

    @Query("""
            select coalesce(sum(bes.grossProfit), 0)
            from BookingExtraService bes
            where bes.booking.status in :statuses
            """)
    BigDecimal sumRecognizedGrossProfit(List<BookingStatus> statuses);
}
