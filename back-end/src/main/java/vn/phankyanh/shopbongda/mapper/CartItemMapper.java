package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.response.CartItemResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartItemMapper {

    CartItem toCartItem(CartItemRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "totalQuantity", source = "totalQuantity")
    @Mapping(target = "sizeCode", source = "size.code")
    @Mapping(target = "productThumbnail", source = "productThumbnail")
    @Mapping(target = "priceAtAddedTime", source = "priceAtAddedTime")
    @Mapping(target = "totalPrice", expression = "java(item.getPriceAtAddedTime() != null ? item.getPriceAtAddedTime().multiply(java.math.BigDecimal.valueOf(item.getTotalQuantity())) : java.math.BigDecimal.ZERO)")
    CartItemResponse toCartItemResponse(CartItem item);
}
