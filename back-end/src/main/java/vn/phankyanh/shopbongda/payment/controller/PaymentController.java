package vn.phankyanh.shopbongda.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.payment.dto.response.PaymentResponse;
import vn.phankyanh.shopbongda.payment.service.PaymentService;
import vn.phankyanh.shopbongda.payment.service.VNPayService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;
    private final VNPayService vnPayService;

    @PostMapping("/api/user/payments/bookings/{bookingId}/vnpay")
    public ApiResponse<String> createPayment(
            @PathVariable Long bookingId,
            HttpServletRequest request
    ) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        if (booking.getPayment().getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Booking already paid");
        }

        String paymentUrl = vnPayService.createPaymentUrl(
                booking.getId(),
                booking.getTotalPrice(),
                request
        );

        ApiResponse<String> response = new ApiResponse<>();

        response.setData(paymentUrl);

        return response;
    }

    @GetMapping("/api/admin/payments")
    public ApiResponse<Page<PaymentResponse>> getAllPayment(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        ApiResponse<Page<PaymentResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(paymentService.handleGetAllPayment(pageNo, pageSize, sortBy, sortDir, search));
        apiResponse.setMessage("Get all payments successfully");
        return apiResponse;
    } 

    @GetMapping("/api/public/payments/{id}")
    public ResponseEntity<?> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // Lấy payment theo orderId
    @GetMapping("/api/user/payments/orders/{orderId}")
    public ResponseEntity<?> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    // Xác nhận thanh toán
    @PutMapping("/api/user/payments/orders/{orderId}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long orderId) {
        Payment payment = paymentService.confirmPaymentForOrder(orderId);
        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("/api/admin/payments/{id}")
    public ApiResponse<String> deletePayment(@PathVariable Long id) {
        paymentService.handleDeletePayment(id);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Payment deleted successfully");
        return apiResponse;
    }

}
