package vn.phankyanh.shopbongda.payment.service;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.dto.response.PaymentResponse;

public interface PaymentService {

    Page<PaymentResponse> getAllPayments(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search
    );

    void deletePayment(Long id);

    Payment getPaymentById(Long id);

    Payment getPaymentByOrderId(Long orderId);

    Payment confirmPayment(Long orderId);
}