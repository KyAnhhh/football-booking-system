package vn.phankyanh.shopbongda.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.domain.OrderStatus;
import vn.phankyanh.shopbongda.domain.ProductVariant;
import vn.phankyanh.shopbongda.dto.response.OrderItemResponse;
import vn.phankyanh.shopbongda.mapper.OrderItemMapper;
import vn.phankyanh.shopbongda.repository.OrderItemRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    public List<OrderItemResponse> handleGetOrderItemByOrderId(Long orderId, Long userId) {
        return orderItemRepository
                .findByOrder_IdAndOrder_User_Id(orderId, userId)
                .stream()
                .map(orderItemMapper::toOrderItemResponse)
                .toList();
    }


    public OrderItemResponse handleUpdateQuantity(Long orderItemId, Integer quantity, Long userId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        Order order = item.getOrder();
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Truy cập bị từ chối");
        }

        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException("Không thể cập nhật đơn hàng sau khi đơn hàng đang được xử lý");
        }

        ProductVariant variant = item.getProductVariant();
        int diff = quantity - item.getQuantity();

        if (variant.getStock() < diff) {
            throw new RuntimeException("Không đủ hàng");
        }

        // Update stock
        variant.setStock(variant.getStock() - diff);
        variant.setSoldQuantity(variant.getSoldQuantity() + diff);

        // Update item
        item.setQuantity(quantity);
        item.setTotal(item.getFinalPrice().multiply(BigDecimal.valueOf(quantity)));

        order.setTotalAmount(order.getOrderItems().stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(order.getShippingCost()));

        return orderItemMapper.toOrderItemResponse(item);
    }

    public void handleDeleteOrderItem(Long orderItemId, Long userId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        Order order = item.getOrder();
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException("Cannot delete order item after order processing");
        }

        // Trả stock
        ProductVariant variant = item.getProductVariant();
        variant.setStock(variant.getStock() + item.getQuantity());
        variant.setSoldQuantity(variant.getSoldQuantity() - item.getQuantity());

        // Xóa item
        order.getOrderItems().remove(item);

        // Cập nhật total đơn
        order.setTotalAmount(order.getOrderItems().stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(order.getShippingCost()));

        orderItemRepository.delete(item);
    }
}
