package vn.phankyanh.shopbongda.order.service;

import java.util.List;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.dto.request.CheckoutRequest;
import vn.phankyanh.shopbongda.order.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.order.dto.response.OrderResponse;

public interface OrderService {

    Page<OrderResponse> getAllOrders(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            OrderStatus status
    );

    OrderResponse getOrderById(Long id, Long userId);

    OrderResponse getOrderForQr(Long orderId, Long userId);

    OrderResponse markPaymentSubmitted(Long orderId, Long userId);

    List<OrderResponse> getMyOrders(Long userId);

    OrderResponse createOrder(OrderRequest request, Long userId);

    void deleteOrder(Long id);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);

    void cancelOrder(Long orderId, Long userId);

    OrderResponse checkout(Long userId, CheckoutRequest request);

    void confirmReceive(Long orderId, Long userId);
}
