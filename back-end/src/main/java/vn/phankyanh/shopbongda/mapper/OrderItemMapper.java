package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.response.OrderItemResponse;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItem toOrderItem(OrderItemRequest request);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    void updateOrderItem(@MappingTarget OrderItem orderItem, OrderItemRequest request);
}
