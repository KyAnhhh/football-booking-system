package vn.phankyanh.shopbongda.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShippingAddressResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
