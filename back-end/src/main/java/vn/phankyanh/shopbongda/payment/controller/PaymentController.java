package vn.phankyanh.shopbongda.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.dto.response.PaymentResponse;
import vn.phankyanh.shopbongda.payment.service.PaymentService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/admin/payments")
    public ApiResponse<Page<PaymentResponse>> getAllPayment(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        ApiResponse<Page<PaymentResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(paymentService.getAllPayments(pageNo, pageSize, sortBy, sortDir, search));
        apiResponse.setMessage("Get all payments successfully");
        return apiResponse;
    } 

    @GetMapping("/admin/payments/{id}")
    public ResponseEntity<?> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // Lấy payment theo orderId
    @GetMapping("/user/payments/orders/{orderId}")
    public ResponseEntity<?> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    // Xác nhận thanh toán
    @PutMapping("/admin/payments/orders/{orderId}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long orderId) {
        Payment payment = paymentService.confirmPayment(orderId);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/admin/payments/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBookingPayment(
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(
                paymentService.confirmBookingPayment(bookingId)
        );
    }

    @DeleteMapping("/admin/payments/{id}")
    public ApiResponse<String> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Payment deleted successfully");
        return apiResponse;
    }

}
