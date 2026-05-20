package vn.phankyanh.shopbongda.order.service;

import java.util.List;

import vn.phankyanh.shopbongda.order.dto.response.OrderItemResponse;

public interface OrderItemService {

    List<OrderItemResponse> getOrderItemByOrderId(
            Long orderId,
            Long userId
    );

    OrderItemResponse updateQuantity(
            Long orderItemId,
            Integer quantity,
            Long userId
    );

    void deleteOrderItem(
            Long orderItemId,
            Long userId
    );
}