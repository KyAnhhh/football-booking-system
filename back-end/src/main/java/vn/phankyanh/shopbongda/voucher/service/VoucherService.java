package vn.phankyanh.shopbongda.voucher.service;

import java.math.BigDecimal;
import java.util.List;
import vn.phankyanh.shopbongda.voucher.dto.request.VoucherRequest;
import vn.phankyanh.shopbongda.voucher.dto.response.VoucherResponse;

public interface VoucherService {

    VoucherResponse create(
            VoucherRequest request
    );

    List<VoucherResponse> getAll();

    VoucherResponse update(
            Long id,
            VoucherRequest request
    );

    VoucherResponse getById(Long id);

    void delete(
            Long id
    );

    VoucherResponse getByCode(String code);

    BigDecimal applyVoucher(
            String code,
            BigDecimal amount
    );

    VoucherApplicationResult consumeVoucher(
            String code,
            BigDecimal amount
    );

    void recordUsage(
            VoucherApplicationResult result,
            Long userId,
            Long bookingId
    );

    void refundVoucherForBooking(
            Long bookingId
    );
}
