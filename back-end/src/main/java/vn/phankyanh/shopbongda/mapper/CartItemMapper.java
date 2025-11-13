package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.response.CartItemResponse;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItem toCartItem(CartItemRequest request);

    CartItemResponse toCartItemResponse(CartItem item);
}

