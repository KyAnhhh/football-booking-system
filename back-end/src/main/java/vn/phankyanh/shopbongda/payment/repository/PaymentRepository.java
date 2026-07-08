package vn.phankyanh.shopbongda.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
   Page<Payment> findByPaymentStatus(String paymentStatus, Pageable pageable);

   long countByPaymentStatusAndPaymentMethod(PaymentStatus paymentStatus, PaymentMethod paymentMethod);

   @Query("""
         select p
         from Payment p
         left join fetch p.order o
         left join fetch o.shippingAddress
         where p.paymentStatus = :paymentStatus
         and p.paymentMethod = :paymentMethod
         order by p.updatedAt desc
         """)
   java.util.List<Payment> findDashboardPayments(
         @Param("paymentStatus") PaymentStatus paymentStatus,
         @Param("paymentMethod") PaymentMethod paymentMethod,
         Pageable pageable
   );
}
