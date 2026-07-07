package vn.phankyanh.shopbongda.payment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import vn.phankyanh.shopbongda.payment.domain.RefundRequest;
import vn.phankyanh.shopbongda.payment.domain.RefundStatus;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from RefundRequest r where r.id = :id")
    Optional<RefundRequest> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
    Optional<RefundRequest> findByPaymentId(Long paymentId);
    List<RefundRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<RefundRequest> findByStatus(RefundStatus status, Pageable pageable);
}
