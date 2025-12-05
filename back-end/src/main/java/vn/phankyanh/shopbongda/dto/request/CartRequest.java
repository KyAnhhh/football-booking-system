package vn.phankyanh.shopbongda.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class CartRequest {

    private List<CartItemRequest> items;

}
