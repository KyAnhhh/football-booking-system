package vn.phankyanh.shopbongda.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShippingAddressResponse {
    private Long id;
    private Long userId;
    private String receiverName;
    private String phone;
    private String addressLine;
    private String ward;
    private String district;
    private String city;
    private String formattedAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
