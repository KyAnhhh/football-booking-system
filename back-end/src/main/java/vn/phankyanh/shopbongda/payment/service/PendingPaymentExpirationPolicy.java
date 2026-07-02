package vn.phankyanh.shopbongda.payment.service;

import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;

public final class PendingPaymentExpirationPolicy {

    private PendingPaymentExpirationPolicy() {
    }

    public static boolean shouldExpire(PaymentStatus paymentStatus) {
        return paymentStatus == PaymentStatus.UNPAID
                || paymentStatus == PaymentStatus.PENDING;
    }
}
