package vn.phankyanh.shopbongda.voucher.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.phankyanh.shopbongda.voucher.domain.Voucher;
import vn.phankyanh.shopbongda.voucher.domain.VoucherUsage;
import vn.phankyanh.shopbongda.voucher.domain.DiscountType;
import vn.phankyanh.shopbongda.voucher.dto.request.VoucherRequest;
import vn.phankyanh.shopbongda.voucher.dto.response.VoucherResponse;
import vn.phankyanh.shopbongda.voucher.repository.VoucherRepository;
import vn.phankyanh.shopbongda.voucher.repository.VoucherUsageRepository;
import vn.phankyanh.shopbongda.voucher.service.VoucherApplicationResult;
import vn.phankyanh.shopbongda.voucher.service.VoucherService;
import vn.phankyanh.shopbongda.voucher.mapper.VoucherMapper;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final VoucherMapper voucherMapper;


    @Override
    @Transactional(readOnly = true)
    public List<VoucherResponse> getAll() {
        return voucherRepository.findAll()
                .stream()
                .map(voucherMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal applyVoucher(
            String code,
            BigDecimal amount
    ) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Invalid amount");
        }

        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException("Voucher not found"));

        return calculateVoucherApplication(
                voucher,
                amount
        ).getFinalAmount();
    }

    @Override
    @Transactional
    public VoucherApplicationResult consumeVoucher(
            String code,
            BigDecimal amount
    ) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Invalid amount");
        }

        Voucher voucher = voucherRepository.findByCodeForUpdate(code)
                .orElseThrow(() ->
                        new RuntimeException("Voucher not found"));

        VoucherApplicationResult result =
                calculateVoucherApplication(
                        voucher,
                        amount
                );

        voucher.setQuantity(
                voucher.getQuantity() - 1
        );

        voucherRepository.save(voucher);

        return result;
    }

    @Override
    @Transactional
    public void recordUsage(
            VoucherApplicationResult result,
            Long userId,
            Long bookingId
    ) {

        if (result == null) {
            return;
        }

        if (voucherUsageRepository.existsByBookingId(bookingId)) {
            throw new RuntimeException(
                    "Voucher already used for this booking");
        }

        VoucherUsage usage =
                VoucherUsage.builder()
                        .voucherId(result.getVoucherId())
                        .bookingId(bookingId)
                        .userId(userId)
                        .voucherCode(result.getVoucherCode())
                        .discountAmount(result.getDiscountAmount())
                        .usedAt(LocalDateTime.now())
                        .build();

        voucherUsageRepository.save(usage);
    }

    private VoucherApplicationResult calculateVoucherApplication(
            Voucher voucher,
            BigDecimal amount
    ) {

        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            throw new RuntimeException("Voucher inactive");
        }

        LocalDateTime now = LocalDateTime.now();

        if (voucher.getStartDate() != null
                && now.isBefore(voucher.getStartDate())) {

            throw new RuntimeException(
                    "Voucher not started yet");
        }

        if (voucher.getEndDate() != null
                && now.isAfter(voucher.getEndDate())) {

            throw new RuntimeException(
                    "Voucher expired");
        }

        if (voucher.getQuantity() == null
                || voucher.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Voucher exhausted");
        }

        BigDecimal discountAmount;

        if (voucher.getDiscountType() == null
                || voucher.getDiscountValue() == null) {

            throw new RuntimeException(
                    "Invalid voucher discount");
        }

        if (voucher.getDiscountType()
                == DiscountType.PERCENT) {

            if (voucher.getDiscountValue()
                    .compareTo(BigDecimal.valueOf(100)) > 0) {

                throw new RuntimeException(
                        "Invalid discount percent");
            }

            discountAmount = amount
                    .multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));

        } else {

            discountAmount =
                    voucher.getDiscountValue();
        }

        BigDecimal finalAmount =
                amount.subtract(discountAmount);

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        BigDecimal actualDiscount =
                amount.subtract(finalAmount);

        return new VoucherApplicationResult(
                voucher.getId(),
                voucher.getCode(),
                finalAmount,
                actualDiscount
        );
    }
    
    @Override
    @Transactional
    public VoucherResponse create(
            VoucherRequest request
    ) {

        if (voucherRepository.existsByCode(
                request.getCode()
        )) {

            throw new RuntimeException(
                    "Voucher code already exists");
        }

        Voucher voucher = voucherMapper.toEntity(request);

        Voucher saved =
                voucherRepository.save(voucher);

        return voucherMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponse getById(Long id) {

        Voucher voucher =
                voucherRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Voucher not found"));

        return voucherMapper.toResponse(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponse getByCode(
            String code
    ) {

        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException("Voucher not found"));

        return voucherMapper.toResponse(voucher);
    }

    @Override
    @Transactional
    public VoucherResponse update(
            Long id,
            VoucherRequest request
    ) {

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Voucher not found"));

        Voucher existing =
                voucherRepository.findByCode(
                        request.getCode()
                ).orElse(null);

        if (existing != null
                && !existing.getId().equals(id)) {

            throw new RuntimeException(
                    "Voucher code already exists");
        }

        voucherMapper.updateEntity(
                request,
                voucher
        );

        Voucher updated =
                voucherRepository.save(voucher);

        return voucherMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(
            Long id
    ) {

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Voucher not found"));

        voucherRepository.delete(voucher);
    }
}
