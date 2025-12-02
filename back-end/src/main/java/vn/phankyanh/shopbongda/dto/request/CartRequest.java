package vn.phankyanh.shopbongda.dto.request;

import lombok.Data;
import vn.phankyanh.shopbongda.domain.CartItem;
import java.util.List;

@Data
public class CartRequest {

    private List<CartItemRequest> items;

}
