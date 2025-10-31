package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true) // ko map field id
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())") // gán giá trị mặt định khi mapping
    Order toOrder(OrderRequest request);

    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "id", ignore = true)
    void updateOrder(@MappingTarget Order order, OrderRequest request);
}
