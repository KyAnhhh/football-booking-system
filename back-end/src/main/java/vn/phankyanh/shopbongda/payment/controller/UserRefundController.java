package vn.phankyanh.shopbongda.payment.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.payment.dto.request.RefundBankAccountRequest;
import vn.phankyanh.shopbongda.payment.dto.response.RefundResponse;
import vn.phankyanh.shopbongda.payment.service.RefundService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/refunds")
public class UserRefundController {

    private final RefundService refundService;

    @GetMapping
    public ApiResponse<List<RefundResponse>> getMyRefunds(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<List<RefundResponse>> response = new ApiResponse<>();
        response.setData(refundService.getMine(userId));
        response.setMessage("Get my refunds successfully");
        return response;
    }

    @PutMapping("/{refundId}/bank-account")
    public ApiResponse<RefundResponse> submitBankAccount(
            @PathVariable Long refundId,
            @Valid @RequestBody RefundBankAccountRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = jwt.getClaim("id");
        ApiResponse<RefundResponse> response = new ApiResponse<>();
        response.setData(refundService.submitBankAccount(refundId, userId, request));
        response.setMessage("Refund bank account submitted successfully");
        return response;
    }
}
