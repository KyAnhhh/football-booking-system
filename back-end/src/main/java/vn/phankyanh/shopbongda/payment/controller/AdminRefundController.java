package vn.phankyanh.shopbongda.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.payment.domain.RefundStatus;
import vn.phankyanh.shopbongda.payment.dto.request.CompleteRefundRequest;
import vn.phankyanh.shopbongda.payment.dto.request.FailRefundRequest;
import vn.phankyanh.shopbongda.payment.dto.request.ReviewRefundPaymentRequest;
import vn.phankyanh.shopbongda.payment.dto.response.RefundResponse;
import vn.phankyanh.shopbongda.payment.service.RefundService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/refunds")
public class AdminRefundController {

    private final RefundService refundService;

    @GetMapping
    public ApiResponse<Page<RefundResponse>> getRefunds(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) RefundStatus status
    ) {
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        ApiResponse<Page<RefundResponse>> response = new ApiResponse<>();
        response.setData(refundService.getAll(status, pageable));
        response.setMessage("Get refund requests successfully");
        return response;
    }

    @PutMapping("/{refundId}/review-payment")
    public ApiResponse<RefundResponse> reviewPayment(
            @PathVariable Long refundId,
            @Valid @RequestBody ReviewRefundPaymentRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("id");
        ApiResponse<RefundResponse> response = new ApiResponse<>();
        response.setData(refundService.reviewPayment(refundId, actorId, request));
        response.setMessage("Refund payment reviewed successfully");
        return response;
    }

    @PutMapping("/{refundId}/processing")
    public ApiResponse<RefundResponse> startProcessing(
            @PathVariable Long refundId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("id");
        ApiResponse<RefundResponse> response = new ApiResponse<>();
        response.setData(refundService.startProcessing(refundId, actorId));
        response.setMessage("Refund processing started successfully");
        return response;
    }

    @PutMapping("/{refundId}/complete")
    public ApiResponse<RefundResponse> complete(
            @PathVariable Long refundId,
            @Valid @RequestBody CompleteRefundRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("id");
        ApiResponse<RefundResponse> response = new ApiResponse<>();
        response.setData(refundService.complete(refundId, actorId, request));
        response.setMessage("Refund completed successfully");
        return response;
    }

    @PutMapping("/{refundId}/failed")
    public ApiResponse<RefundResponse> fail(
            @PathVariable Long refundId,
            @Valid @RequestBody FailRefundRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("id");
        ApiResponse<RefundResponse> response = new ApiResponse<>();
        response.setData(refundService.fail(refundId, actorId, request));
        response.setMessage("Refund marked as failed successfully");
        return response;
    }
}
