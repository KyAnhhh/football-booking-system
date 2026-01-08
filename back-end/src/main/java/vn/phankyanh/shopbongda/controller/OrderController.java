package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderStatus;
import vn.phankyanh.shopbongda.dto.request.CheckoutRequest;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/public/orders")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleCreateOrder(request, userId));
        return apiResponse;
    }

    @GetMapping("/api/admin/orders")
    public ApiResponse<Page<OrderResponse>> getAllOrder(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) OrderStatus status){
        ApiResponse<Page<OrderResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(orderService.handleGetAllOrder(pageNo, pageSize, sortBy, sortDir, search, status));
        return apiResponse;
    }

    @GetMapping("/api/public/orders/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleGetOrderById(id, userId));
        return apiResponse;
    }

    @GetMapping("/api/public/orders/me")
    public ApiResponse<List<OrderResponse>> getMyOrder(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<List<OrderResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleGetMyOrders(userId));
        return apiResponse;
    }

    @PutMapping("/api/public/orders/{id}")
    public ApiResponse<Order> updateOrder(@PathVariable("id") Long id, @RequestBody OrderRequest request, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        ApiResponse<Order> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleUpdateOrder(id, request, userId));
        return apiResponse;
    }

    @PutMapping("/api/admin/orders/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable("id") Long id, @RequestBody OrderStatus status){
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleUpdateOrderStatus(id, status));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/orders/{id}")
    public ApiResponse<String> deleteOrder(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.orderService.handleDeleteOrder(id);
        apiResponse.setMessage("Deleted order with id: " + id);
        return apiResponse;
    }

    @PutMapping("/api/public/orders/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable("orderId") Long orderId) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        this.orderService.handleCancelOrder(orderId);
        apiResponse.setMessage("Order cancelled successfully");
        return apiResponse;
    }

    @PutMapping("api/public/orders")
    public ApiResponse<OrderResponse> handleCheckout(@AuthenticationPrincipal Jwt jwt, @RequestBody CheckoutRequest request) {
        Long userId = jwt.getClaim("id");
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleCheckout(userId, request));
        return apiResponse;
    }

}
