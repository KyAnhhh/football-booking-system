package vn.phankyanh.shopbongda.payment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    COD,
    CASH,
    VIETQR;

    @JsonCreator
    public static PaymentMethod from(String value) {
        return PaymentMethod.valueOf(value.toUpperCase());
    }
}
