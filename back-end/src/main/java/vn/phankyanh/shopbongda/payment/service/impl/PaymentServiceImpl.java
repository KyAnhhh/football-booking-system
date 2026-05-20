package vn.phankyanh.shopbongda.payment.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.Order;
import vn.phankyanh.shopbongda.order.repository.OrderRepository;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.payment.dto.response.PaymentResponse;
import vn.phankyanh.shopbongda.payment.mapper.PaymentMapper;
import vn.phankyanh.shopbongda.payment.repository.PaymentRepository;
import vn.phankyanh.shopbongda.payment.service.PaymentService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Page<PaymentResponse> getAllPayments(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search
    ) {

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Payment> paymentPage;

        if (search != null && !search.isBlank()) {
            paymentPage = paymentRepository.findByPaymentStatus(search, pageable);
        } else {
            paymentPage = paymentRepository.findAll(pageable);
        }

        return paymentPage.map(paymentMapper::toPaymentResponse);
    }

    @Override
    public void deletePayment(Long id) {

        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found");
        }

        paymentRepository.deleteById(id);
    }

    @Override
    public Payment getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getPayment();
    }

    @Override
    public Payment confirmPayment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = order.getPayment();

        if (payment == null) {
            throw new RuntimeException("Order has no payment");
        }

        payment.setPaymentStatus(PaymentStatus.PAID);

        payment.setPaymentDate(LocalDateTime.now());

        orderRepository.save(order);

        return paymentRepository.save(payment);
    }
}