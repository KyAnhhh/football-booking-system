package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment createPayment(BigDecimal totalAmount, String method, String type) {
        Payment payment = Payment.builder()
                .totalAmount(totalAmount)
                .status("Chưa thanh toán")
                .method(method)
                .type(type)
                .build();
        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Id not found"));
    }
}

