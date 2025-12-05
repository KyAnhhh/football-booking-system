package vn.phankyanh.shopbongda.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long shippingAddressId;
    private Integer shippingCost;
    private List<OrderItemRequest> items;
}
