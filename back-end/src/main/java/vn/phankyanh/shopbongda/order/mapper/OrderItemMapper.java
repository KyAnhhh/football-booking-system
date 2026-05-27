package vn.phankyanh.shopbongda.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.order.dto.response.OrderItemResponse;
import vn.phankyanh.shopbongda.order.dto.request.OrderItemRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @Mapping(target = "productId", ignore = true) 
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "unitPrice", ignore = true) 
    @Mapping(target = "discountAmount", ignore = true) 
    @Mapping(target = "finalPrice", ignore = true) 
    @Mapping(target = "total", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderItem toOrderItem(OrderItemRequest request);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "sizeName", target = "sizeName")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(source = "discountAmount", target = "discountAmount")
    @Mapping(source = "finalPrice", target = "finalPrice")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    void updateOrderItem(@MappingTarget OrderItem orderItem, OrderItemRequest request);
}
