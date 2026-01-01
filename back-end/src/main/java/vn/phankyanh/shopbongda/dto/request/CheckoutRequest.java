package vn.phankyanh.shopbongda.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class CheckoutRequest {
    private List<Long> cartItemIds;
    private Long shippingAddressId;
    private String paymentMethod;
}
