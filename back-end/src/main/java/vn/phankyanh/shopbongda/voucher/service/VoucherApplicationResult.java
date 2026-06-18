package vn.phankyanh.shopbongda.voucher.service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoucherApplicationResult {

    private final Long voucherId;

    private final String voucherCode;

    private final BigDecimal finalAmount;

    private final BigDecimal discountAmount;
}
