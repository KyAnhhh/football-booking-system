package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.domain.Cart;
import vn.phankyanh.shopbongda.dto.request.CartRequest;
import vn.phankyanh.shopbongda.dto.response.CartResponse;


@Mapper(componentModel = "spring")
public interface CartMapper {

    Cart toCart(CartRequest request);

    @Mapping(target = "totalAmount", expression = "java(cart.getItems().stream().mapToInt(i -> i.getTotalQuantity()).sum())")
    @Mapping(target = "totalPrice", expression = "java(cart.getItems().stream().map(i -> i.getPriceAtAddedTime().multiply(new java.math.BigDecimal(i.getTotalQuantity()))).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))")
    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "id", ignore = true)
    void updateCart(@MappingTarget Cart cart, CartRequest request);
}
