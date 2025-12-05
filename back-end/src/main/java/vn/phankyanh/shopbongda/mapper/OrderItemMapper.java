package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.response.OrderItemResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItem toOrderItem(OrderItemRequest request);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "productName", target = "productName")
    @Mapping(
            target = "total",
            expression = "java(orderItem.getPrice().multiply(java.math.BigDecimal.valueOf(orderItem.getQuantity())))"
    )
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    void updateOrderItem(@MappingTarget OrderItem orderItem, OrderItemRequest request);
}
