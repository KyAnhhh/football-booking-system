package vn.phankyanh.shopbongda.payment.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;

class PendingPaymentExpirationPolicyTest {

    @Test
    void expiresOnlyUnpaidOrPendingPayments() {
        assertTrue(PendingPaymentExpirationPolicy.shouldExpire(PaymentStatus.UNPAID));
        assertTrue(PendingPaymentExpirationPolicy.shouldExpire(PaymentStatus.PENDING));
        assertFalse(PendingPaymentExpirationPolicy.shouldExpire(PaymentStatus.PAID));
        assertFalse(PendingPaymentExpirationPolicy.shouldExpire(PaymentStatus.REFUNDED));
        assertFalse(PendingPaymentExpirationPolicy.shouldExpire(PaymentStatus.FAILED));
    }
}
