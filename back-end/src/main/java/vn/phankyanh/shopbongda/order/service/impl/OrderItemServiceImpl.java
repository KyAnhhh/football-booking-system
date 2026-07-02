package vn.phankyanh.shopbongda.order.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.Order;
import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.order.dto.response.OrderItemResponse;
import vn.phankyanh.shopbongda.order.mapper.OrderItemMapper;
import vn.phankyanh.shopbongda.order.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.order.service.OrderItemService;

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

        throw new IllegalStateException(
                "Không thể sửa sản phẩm sau khi checkout; hãy hủy đơn và đặt lại"
        );
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

        throw new IllegalStateException(
                "Không thể xóa sản phẩm sau khi checkout; hãy hủy đơn và đặt lại"
        );
    }
}
