package vn.phankyanh.shopbongda.payment.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.payment.domain.*;
import vn.phankyanh.shopbongda.payment.dto.request.*;
import vn.phankyanh.shopbongda.payment.dto.response.RefundResponse;
import vn.phankyanh.shopbongda.payment.repository.RefundRequestRepository;
import vn.phankyanh.shopbongda.payment.service.RefundService;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service @RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final RefundRequestRepository refundRepository;
    private final UserRepository userRepository;

    @Override @Transactional
    public RefundRequest createForCancellation(Payment payment, User user,
            Long orderId, Long bookingId, String reason) {
        RefundRequest existing = refundRepository.findByPaymentId(payment.getId()).orElse(null);
        if (existing != null) return existing;
        RefundStatus status;
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            payment.setPaymentStatus(PaymentStatus.REFUND_PENDING);
            status = RefundStatus.AWAITING_INFORMATION;
        } else if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            status = RefundStatus.PAYMENT_REVIEW;
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            return null;
        }
        return refundRepository.save(RefundRequest.builder()
                .payment(payment).user(user).orderId(orderId).bookingId(bookingId)
                .amount(payment.getTotalAmount()).reason(reason).status(status).build());
    }

    @Override @Transactional(readOnly = true)
    public List<RefundResponse> getMine(Long userId) {
        return refundRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(refund -> toResponse(refund, false)).toList();
    }

    @Override @Transactional(readOnly = true)
    public Page<RefundResponse> getAll(RefundStatus status, Pageable pageable) {
        Page<RefundRequest> page = status == null
                ? refundRepository.findAll(pageable) : refundRepository.findByStatus(status, pageable);
        return page.map(refund -> toResponse(refund, true));
    }

    @Override @Transactional
    public RefundResponse submitBankAccount(Long id, Long userId, RefundBankAccountRequest request) {
        RefundRequest refund = locked(id);
        if (!refund.getUser().getId().equals(userId)) throw new RuntimeException("Refund not found");
        if (refund.getStatus() != RefundStatus.AWAITING_INFORMATION
                && refund.getStatus() != RefundStatus.FAILED) {
            throw new IllegalStateException("Refund bank information cannot be changed");
        }
        refund.setBankCode(request.getBankCode().trim());
        refund.setBankAccountNumber(request.getBankAccountNumber().trim());
        refund.setBankAccountName(request.getBankAccountName().trim().toUpperCase());
        refund.setFailureReason(null);
        refund.setStatus(RefundStatus.PENDING);
        return toResponse(refundRepository.save(refund), false);
    }

    @Override @Transactional
    public RefundResponse reviewPayment(Long id, Long actorId, ReviewRefundPaymentRequest request) {
        RefundRequest refund = locked(id);
        if (refund.getStatus() != RefundStatus.PAYMENT_REVIEW) {
            throw new IllegalStateException("Refund is not waiting for payment review");
        }
        refund.setProcessedBy(actor(actorId));
        if (Boolean.TRUE.equals(request.getPaymentReceived())) {
            refund.getPayment().setPaymentStatus(PaymentStatus.REFUND_PENDING);
            refund.setStatus(RefundStatus.AWAITING_INFORMATION);
        } else {
            refund.getPayment().setPaymentStatus(PaymentStatus.FAILED);
            refund.setStatus(RefundStatus.FAILED);
            refund.setFailureReason("Payment was not received");
            refund.setProcessedAt(LocalDateTime.now());
        }
        return toResponse(refundRepository.save(refund), true);
    }

    @Override @Transactional
    public RefundResponse startProcessing(Long id, Long actorId) {
        RefundRequest refund = locked(id);
        if (refund.getStatus() != RefundStatus.PENDING
                && refund.getStatus() != RefundStatus.FAILED) {
            throw new IllegalStateException("Refund is not ready for processing");
        }
        requireBank(refund);
        refund.setStatus(RefundStatus.PROCESSING);
        refund.setProcessedBy(actor(actorId));
        refund.setFailureReason(null);
        return toResponse(refundRepository.save(refund), true);
    }

    @Override @Transactional
    public RefundResponse complete(Long id, Long actorId, CompleteRefundRequest request) {
        RefundRequest refund = locked(id);
        if (refund.getStatus() == RefundStatus.COMPLETED) return toResponse(refund, true);
        if (refund.getStatus() != RefundStatus.PROCESSING) {
            throw new IllegalStateException("Refund must be processing before completion");
        }
        requireBank(refund);
        refund.setStatus(RefundStatus.COMPLETED);
        refund.setRefundTransactionCode(request.getRefundTransactionCode().trim());
        refund.setAdminNote(request.getAdminNote());
        refund.setProcessedBy(actor(actorId));
        refund.setProcessedAt(LocalDateTime.now());
        refund.getPayment().setPaymentStatus(PaymentStatus.REFUNDED);
        refund.getPayment().setPaymentDate(LocalDateTime.now());
        return toResponse(refundRepository.save(refund), true);
    }

    @Override @Transactional
    public RefundResponse fail(Long id, Long actorId, FailRefundRequest request) {
        RefundRequest refund = locked(id);
        if (refund.getStatus() != RefundStatus.PROCESSING) {
            throw new IllegalStateException("Only a processing refund can fail");
        }
        refund.setStatus(RefundStatus.FAILED);
        refund.setFailureReason(request.getReason().trim());
        refund.setProcessedBy(actor(actorId));
        return toResponse(refundRepository.save(refund), true);
    }

    private RefundRequest locked(Long id) {
        return refundRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RuntimeException("Refund not found"));
    }
    private User actor(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
    private void requireBank(RefundRequest r) {
        if (r.getBankAccountNumber() == null || r.getBankCode() == null || r.getBankAccountName() == null)
            throw new IllegalStateException("Refund bank information is required");
    }
    private RefundResponse toResponse(RefundRequest r, boolean reveal) {
        String account = r.getBankAccountNumber();
        if (!reveal && account != null && account.length() > 4) {
            account = "****" + account.substring(account.length() - 4);
        }
        return RefundResponse.builder().id(r.getId()).paymentId(r.getPayment().getId())
                .orderId(r.getOrderId()).bookingId(r.getBookingId()).userId(r.getUser().getId())
                .username(r.getUser().getUsername()).amount(r.getAmount()).reason(r.getReason())
                .status(r.getStatus()).bankCode(r.getBankCode()).bankAccountNumber(account)
                .bankAccountName(r.getBankAccountName()).refundTransactionCode(r.getRefundTransactionCode())
                .adminNote(r.getAdminNote()).failureReason(r.getFailureReason())
                .requestedAt(r.getRequestedAt()).processedAt(r.getProcessedAt()).build();
    }
}
