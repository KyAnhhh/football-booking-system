package vn.phankyanh.shopbongda.booking.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingExtraServiceResponse {

    private Long serviceId;

    private String serviceName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}
