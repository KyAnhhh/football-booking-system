package vn.phankyanh.shopbongda.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressRequest {
    private String fullName;
    private String phone;
    private String address;
}
