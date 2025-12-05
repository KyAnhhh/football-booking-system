package vn.phankyanh.shopbongda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.dto.request.UpdateStatusRequest;
import vn.phankyanh.shopbongda.service.PaymentService;

@RestController
@RequestMapping("/api/public/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // Cập nhật status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        Payment payment = paymentService.updatePaymentStatus(id, request.getStatus());
        return ResponseEntity.ok(payment);
    }

    // Lấy payment theo orderId
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    // Xác nhận thanh toán
    @PutMapping("/orders/{orderId}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long orderId) {
        Payment payment = paymentService.confirmPaymentForOrder(orderId);
        return ResponseEntity.ok(payment);
    }
}
