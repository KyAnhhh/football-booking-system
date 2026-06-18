package vn.phankyanh.shopbongda.voucher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.voucher.domain.VoucherUsage;

@Repository
public interface VoucherUsageRepository
        extends JpaRepository<VoucherUsage, Long> {

    boolean existsByBookingId(Long bookingId);
}
