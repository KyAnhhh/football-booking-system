package vn.phankyanh.shopbongda.order.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.dto.request.CheckoutRequest;
import vn.phankyanh.shopbongda.order.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.order.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.order.service.OrderService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/orders")
public class UserOrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @RequestBody OrderRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = jwt.getClaim("id");

        ApiResponse<OrderResponse> apiResponse =
                new ApiResponse<>();

        apiResponse.setData(
                orderService.createOrder(request, userId)
        );

        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = jwt.getClaim("id");

        ApiResponse<OrderResponse> apiResponse =
                new ApiResponse<>();

        apiResponse.setData(
                orderService.getOrderById(id, userId)
        );

        return apiResponse;
    }

    @GetMapping("/me")
    public ApiResponse<List<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = jwt.getClaim("id");

        ApiResponse<List<OrderResponse>> apiResponse =
                new ApiResponse<>();

        apiResponse.setData(
                orderService.getMyOrders(userId)
        );

        return apiResponse;
    }

    @PostMapping("/checkout-cart")
    public ApiResponse<OrderResponse> checkout(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CheckoutRequest request
    ) {

        Long userId = jwt.getClaim("id");

        ApiResponse<OrderResponse> apiResponse =
                new ApiResponse<>();

        apiResponse.setData(
                orderService.checkout(userId, request)
        );

        return apiResponse;
    }

    @PutMapping("/{orderId}/confirm-receipt")
    public ApiResponse<Void> confirmReceive(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = jwt.getClaim("id");

        orderService.confirmReceive(orderId, userId);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>();

        apiResponse.setMessage(
                "Order receipt confirmed successfully"
        );

        return apiResponse;
    }

    @PutMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = jwt.getClaim("id");

        orderService.cancelOrder(orderId, userId);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>();

        apiResponse.setMessage(
                "Order cancelled successfully"
        );

        return apiResponse;
    }
}