package vn.phankyanh.shopbongda.voucher.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.voucher.domain.VoucherUsage;

@Repository
public interface VoucherUsageRepository
        extends JpaRepository<VoucherUsage, Long> {

    boolean existsByBookingId(Long bookingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT vu FROM VoucherUsage vu
    WHERE vu.bookingId = :bookingId
    """)
    Optional<VoucherUsage> findByBookingIdForUpdate(
            @Param("bookingId") Long bookingId
    );
}
