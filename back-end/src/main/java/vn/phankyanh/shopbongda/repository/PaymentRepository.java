package vn.phankyanh.shopbongda.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
   Page<Payment> findByPaymentStatus(String paymentStatus, Pageable pageable);
}