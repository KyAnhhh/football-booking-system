package vn.phankyanh.shopbongda.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.repository.OrderRepository;
import vn.phankyanh.shopbongda.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

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
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    // Lấy payment theo orderId
    public Payment getPaymentByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getPayment();
    }

    // Xác nhận thanh toán
    public Payment confirmPaymentForOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = order.getPayment();

        if (payment == null) {
            throw new RuntimeException("Order has no payment");
        }

         // cập nhật payment
        payment.setStatus("Đã thanh toán");
        payment.setPaymentDate(LocalDateTime.now());

        // cập nhật order
        // order.setStatus("Đã giao hàng"); // nếu bạn muốn chuyển sang giao hàng
        // order.setDeliveryDate(LocalDateTime.now());

        orderRepository.save(order);

        return paymentRepository.save(payment);
    }
}
