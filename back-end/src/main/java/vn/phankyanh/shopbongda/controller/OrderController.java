package vn.phankyanh.shopbongda.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.request.UpdateStatusRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/public/orders")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request, @RequestHeader("Authorization") String authHeader) throws ParseException {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleCreateOrder(request, authHeader));
        return apiResponse;
    }

    @GetMapping("/api/admin/orders")
    public ApiResponse<List<OrderResponse>> getAllOrder(){
        ApiResponse<List<OrderResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleGetAllOrder());
        return apiResponse;
    }

    @GetMapping("/api/public/orders/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable("id") Long id){
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleGetOrderById(id));
        return apiResponse;
    }

    @GetMapping("/api/public/orders/my")
    public ApiResponse<List<OrderResponse>> getMyOrder(@RequestHeader("Authorization") String authHeader) throws ParseException {
        ApiResponse<List<OrderResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleGetMyOrders(authHeader));
        return apiResponse;
    }

    @PutMapping("/api/public/orders/{id}")
    public ApiResponse<Order> updateOrder(@PathVariable("id") Long id, @RequestBody OrderRequest request){
        ApiResponse<Order> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleUpdateOrder(id, request));
        return apiResponse;
    }

    @PutMapping("/api/admin/orders/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest request){
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleUpdateOrderStatus(id, request.getStatus()));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/orders/{id}")
    public ApiResponse<String> deleteOrder(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.orderService.handleDeleteOrder(id);
        apiResponse.setMessage("Deleted order with id: " + id);
        return apiResponse;
    }

    // Checkout giỏ hàng thành Order
    @PostMapping("/api/public/orders/checkout-cart")
    public ApiResponse<OrderResponse> checkoutCart(
            @RequestParam Long cartId,
            @RequestParam Long shippingAddressId,
            @RequestHeader("Authorization") String authHeader
    ) throws ParseException {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleCheckoutCart(cartId, shippingAddressId, authHeader));
        return apiResponse;
    }
}
