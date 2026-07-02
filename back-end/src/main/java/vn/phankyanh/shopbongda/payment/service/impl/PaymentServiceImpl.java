package vn.phankyanh.shopbongda.payment.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.Order;
import vn.phankyanh.shopbongda.order.repository.OrderRepository;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;
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
    private final BookingRepository bookingRepository;

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
        if (orderRepository.existsByPaymentId(id)
                || bookingRepository.existsByPaymentId(id)) {
            throw new IllegalStateException(
                    "Không thể xóa payment đã gắn với booking hoặc order"
            );
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

        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getPayment();
    }

    @Override
    @Transactional
    public Payment confirmPayment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = order.getPayment();

        if (payment == null) {
            throw new RuntimeException("Order has no payment");
        }
        if (order.getPaymentMethod() != PaymentMethod.VIETQR
                || payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Chỉ có thể xác nhận giao dịch VietQR đang chờ"
            );
        }

        payment.setPaymentStatus(PaymentStatus.PAID);

        payment.setPaymentDate(LocalDateTime.now());

        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment confirmBookingPayment(Long bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));
        Payment payment = booking.getPayment();
        if (booking.getStatus() != BookingStatus.PENDING
                || payment == null
                || payment.getPaymentMethod() != PaymentMethod.VIETQR
                || payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Booking không có giao dịch VietQR đang chờ"
            );
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
}
