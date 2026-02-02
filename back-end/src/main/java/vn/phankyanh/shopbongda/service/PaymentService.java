package vn.phankyanh.shopbongda.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.domain.PaymentStatus;
import vn.phankyanh.shopbongda.dto.response.PaymentResponse;
import vn.phankyanh.shopbongda.mapper.PaymentMapper;
import vn.phankyanh.shopbongda.repository.OrderRepository;
import vn.phankyanh.shopbongda.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    public Page<PaymentResponse> handleGetAllPayment(int pageNo, int pageSize, String sortBy, String sortDir, String search){
        Sort.Direction direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        
        Page<Payment> paymentPage;
        if(search != null && !search.isEmpty()){
            paymentPage = paymentRepository.findByPaymentStatus(search, pageable);
        } else {
            paymentPage = paymentRepository.findAll(pageable);
        }
        return paymentPage.map(paymentMapper::toPaymentResponse);
    }

    //delete
    public void handleDeletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found");
        }
        this.paymentRepository.deleteById(id);
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
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());

        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

}
