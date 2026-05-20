package vn.phankyanh.shopbongda.order.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.Order;
import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.dto.response.OrderItemResponse;
import vn.phankyanh.shopbongda.order.mapper.OrderItemMapper;
import vn.phankyanh.shopbongda.order.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.order.service.OrderItemService;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemResponse> getOrderItemByOrderId(
            Long orderId,
            Long userId
    ) {

        return orderItemRepository
                .findByOrder_IdAndOrder_User_Id(orderId, userId)
                .stream()
                .map(orderItemMapper::toOrderItemResponse)
                .toList();
    }

    @Override
    public OrderItemResponse updateQuantity(
            Long orderItemId,
            Integer quantity,
            Long userId
    ) {

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() ->
                        new RuntimeException("OrderItem not found"));

        Order order = item.getOrder();

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Truy cập bị từ chối");
        }

        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException(
                    "Không thể cập nhật đơn hàng sau khi đơn hàng đang được xử lý"
            );
        }

        ProductVariant variant = item.getProductVariant();

        int diff = quantity - item.getQuantity();

        if (variant.getStock() < diff) {
            throw new RuntimeException("Không đủ hàng");
        }

        variant.setStock(variant.getStock() - diff);

        variant.setSoldQuantity(
                variant.getSoldQuantity() + diff
        );

        item.setQuantity(quantity);

        item.setTotal(
                item.getFinalPrice()
                        .multiply(BigDecimal.valueOf(quantity))
        );

        order.setTotalAmount(
                order.getOrderItems().stream()
                        .map(OrderItem::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .add(order.getShippingCost())
        );

        return orderItemMapper.toOrderItemResponse(item);
    }

    @Override
    public void deleteOrderItem(
            Long orderItemId,
            Long userId
    ) {

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() ->
                        new RuntimeException("OrderItem not found"));

        Order order = item.getOrder();

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException(
                    "Cannot delete order item after order processing"
            );
        }

        ProductVariant variant = item.getProductVariant();

        variant.setStock(
                variant.getStock() + item.getQuantity()
        );

        variant.setSoldQuantity(
                variant.getSoldQuantity() - item.getQuantity()
        );

        order.getOrderItems().remove(item);

        order.setTotalAmount(
                order.getOrderItems().stream()
                        .map(OrderItem::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .add(order.getShippingCost())
        );

        orderItemRepository.delete(item);
    }
}