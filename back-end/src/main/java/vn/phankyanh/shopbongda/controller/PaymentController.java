package vn.phankyanh.shopbongda.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.PaymentResponse;
import vn.phankyanh.shopbongda.service.PaymentService;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

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
    @GetMapping("/api/public/payments/orders/{orderId}")
    public ResponseEntity<?> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    // Xác nhận thanh toán
    @PutMapping("/api/public/payments/orders/{orderId}/confirm")
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

    // Cập nhật status
    // @PutMapping("/order/{orderId}/status")
    // public ResponseEntity<String> updateStatus(
    //         @PathVariable("orderId") Long orderId,
    //         @RequestBody PaymentStatus status) { 
    //     this.paymentService.updatePaymentStatus(orderId, status);
    //     return ResponseEntity.ok("Payment status updated successfully");
    // }
}
