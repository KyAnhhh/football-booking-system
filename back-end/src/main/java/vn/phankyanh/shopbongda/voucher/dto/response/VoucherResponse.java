package vn.phankyanh.shopbongda.voucher.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.voucher.domain.DiscountType;

@Getter
@Setter
@Builder
public class VoucherResponse {

    private Long id;

    private String code;

    private String description;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private Integer quantity;

    private Boolean isActive;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
