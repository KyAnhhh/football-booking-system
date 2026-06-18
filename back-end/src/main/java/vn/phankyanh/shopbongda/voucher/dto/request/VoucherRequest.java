package vn.phankyanh.shopbongda.voucher.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.voucher.domain.DiscountType;

@Getter
@Setter
public class VoucherRequest {

    private String code;

    private String description;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private Integer quantity;

    private Boolean isActive;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
