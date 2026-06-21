package vn.phankyanh.shopbongda.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long paymentId;
    private Long fieldId;
    private String fieldName;
    private String fieldAddress;
    private String fieldImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String contactPhone;
    private String note;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private BigDecimal finalPrice;
    private BigDecimal originalPrice;
    private BigDecimal extraServicePrice;
    private BigDecimal membershipDiscount;
    private BigDecimal voucherDiscount;
    private String voucherCode;
    private List<BookingExtraServiceResponse> extraServices;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionCode;
    private LocalDateTime paymentDate;
}
