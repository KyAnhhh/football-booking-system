package vn.phankyanh.shopbongda.order.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class CartRequest {

    private List<CartItemRequest> items;

}
