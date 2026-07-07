package vn.phankyanh.shopbongda.payment.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.RefundRequest;
import vn.phankyanh.shopbongda.payment.domain.RefundStatus;
import vn.phankyanh.shopbongda.payment.dto.request.*;
import vn.phankyanh.shopbongda.payment.dto.response.RefundResponse;
import vn.phankyanh.shopbongda.user.domain.User;

public interface RefundService {
    RefundRequest createForCancellation(Payment payment, User user, Long orderId, Long bookingId, String reason);
    List<RefundResponse> getMine(Long userId);
    Page<RefundResponse> getAll(RefundStatus status, Pageable pageable);
    RefundResponse submitBankAccount(Long id, Long userId, RefundBankAccountRequest request);
    RefundResponse reviewPayment(Long id, Long actorId, ReviewRefundPaymentRequest request);
    RefundResponse startProcessing(Long id, Long actorId);
    RefundResponse complete(Long id, Long actorId, CompleteRefundRequest request);
    RefundResponse fail(Long id, Long actorId, FailRefundRequest request);
}
