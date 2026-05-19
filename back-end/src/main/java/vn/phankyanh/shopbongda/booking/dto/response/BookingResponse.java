package vn.phankyanh.shopbongda.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingResponse {
    private Long id;
    private Long userId;
    private Long paymentId;
    private Long fieldId;
    private String fieldName;
    private String fieldAddress;
    private String fieldImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalPrice;
    private String status;
}
